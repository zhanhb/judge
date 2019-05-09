/*
 * Copyright 2017-2019 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.util.excel

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import com.google.common.collect.Maps
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.*
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList

/**
 *
 * @author zhanhb
 */
object ExcelUtil {

    @Throws(IOException::class)
    fun <T> toResponse(type: Class<T>, content: Collection<T>,
                       locale: Locale, resultType: ExcelType, name: String?,
                       response: HttpServletResponse) {
        resultType.createWorkBook().use { workbook ->
            buildExcelDocument(type, content.stream(), locale, workbook)
            if (name.isNullOrBlank()) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment")
            } else {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                        .builder("attachment")
                        .filename("$name.${resultType.extension}", StandardCharsets.UTF_8)
                        .build().toString())
            }
            response.contentType = resultType.mediaType.toString()
            response.outputStream.use { out -> workbook.write(out) }
        }
    }

    private fun <T> buildExcelDocument(elementType: Class<T>, content: Stream<T>, locale: Locale, workbook: Workbook) {
        val meta = MetaInfo.forType(elementType, locale)
        val sheet = workbook.createSheet(elementType.simpleName)
        create(meta.headerAsStream, sheet.createRow(0))
        val counter = AtomicInteger()
        content.forEach { entity -> create(meta.fieldsAsStream.map { field -> field.get(entity) }, sheet.createRow(counter.incrementAndGet())) }
    }

    private fun create(stream: Stream<*>, row: Row) {
        val counter = AtomicInteger()
        stream.forEach { value ->
            if (value != null) {
                if (value is String) {
                    row.createCell(counter.getAndIncrement(), CellType.STRING).setCellValue(value)
                } else if (value is Number) {
                    row.createCell(counter.getAndIncrement(), CellType.NUMERIC).setCellValue(value.toDouble())
                } else if (value is Boolean) {
                    row.createCell(counter.getAndIncrement(), CellType.BOOLEAN).setCellValue(value)
                } else if (value is Date) {
                    row.createCell(counter.getAndIncrement(), CellType.NUMERIC).setCellValue(value)
                } else if (value is Calendar) {
                    row.createCell(counter.getAndIncrement(), CellType.NUMERIC).setCellValue(value)
                } else {
                    row.createCell(counter.getAndIncrement(), CellType.ERROR)
                }
            } else {
                row.createCell(counter.getAndIncrement(), CellType.BLANK)
            }
        }
    }

    fun <T> parse(inputStream: InputStream, type: Class<T>, locale: Locale): List<T> {
        val support = inputStream.markSupported()
        val `is` = if (support) inputStream else BufferedInputStream(inputStream)
        try {
            WorkbookFactory.create(`is`).use { workbook ->
                val evaluator = workbook.creationHelper.createFormulaEvaluator()
                return parse(workbook, evaluator, type, locale)
            }
        } catch (ex: IOException) {
            throw BusinessException(BusinessCode.INVALID_EXCEL)
        } catch (ex: IllegalStateException) {
            throw BusinessException(BusinessCode.INVALID_EXCEL)
        }

    }

    private fun <T> parse(workbook: Workbook, evaluator: FormulaEvaluator, type: Class<T>, locale: Locale): List<T> {
        val metaInfo = MetaInfo.forType(type, locale)
        val sheet = workbook.getSheetAt(workbook.activeSheetIndex)
        val rows = sheet.rowIterator()
        if (!rows.hasNext()) {
            return emptyList()
        }
        val firstRow = rows.next()
        val columnIndexToFieldName = Maps.newHashMapWithExpectedSize<Int, String>(metaInfo.size())
        val it = firstRow.cellIterator()
        while (it.hasNext()) {
            val cell = it.next()
            val jsonElement = parseAsJsonElement(cell, evaluator)
            if (jsonElement != null) {
                val field = metaInfo.getField(jsonElement.asString)
                if (field != null) {
                    val name = field.name
                    val index = cell.columnIndex
                    columnIndexToFieldName.put(index, name)
                }
            }
        }
        if (columnIndexToFieldName.isEmpty()) {
            return emptyList()
        }
        val result = ArrayList<T>(sheet.lastRowNum - sheet.firstRowNum)
        while (rows.hasNext()) {
            result.add(parseRow(evaluator, rows.next(), columnIndexToFieldName, type))
        }
        return result
    }

    private fun parseAsJsonElement(cell: Cell, evaluator: FormulaEvaluator): JsonElement? {
        when (cell.cellType) {
            CellType.NUMERIC -> return if (HSSFDateUtil.isCellDateFormatted(cell)) {
                JsonPrimitive(DateFormatterHolder.FORMATTER.format(cell.dateCellValue.toInstant()))
            } else {
                JsonPrimitive(cell.numericCellValue)
            }
            CellType.STRING -> return JsonPrimitive(cell.stringCellValue)
            CellType.FORMULA -> {
                val cellValue = evaluator.evaluate(cell)
                when (cellValue.cellType) {
                    CellType.NUMERIC -> return JsonPrimitive(cellValue.numberValue)
                    CellType.STRING -> return JsonPrimitive(cellValue.stringValue)
                    CellType.BLANK -> return JsonPrimitive("")
                    CellType.BOOLEAN -> return JsonPrimitive(cellValue.booleanValue)
                    CellType.ERROR -> return null
                    else -> return null
                }
            }
            CellType.BLANK -> return JsonPrimitive("")
            CellType.BOOLEAN -> return JsonPrimitive(cell.booleanCellValue)
            CellType.ERROR -> return null
            else -> return null
        }
    }

    private fun <T> parseRow(evaluator: FormulaEvaluator, row: Row, fields: Map<Int, String>, type: Class<T>): T {
        val jsonObject = JsonObject()
        val it = row.cellIterator()
        while (it.hasNext()) {
            val cell = it.next()
            val name = fields[cell.columnIndex]
            if (name != null) {
                val cellValue = parseAsJsonElement(cell, evaluator)
                if (cellValue != null) {
                    jsonObject.add(name, cellValue)
                }
            }
        }
        return GsonHolder.GSON.fromJson(jsonObject, type)
    }

    private object GsonHolder {
        val GSON = GsonBuilder().setDateFormat("yyyy-M-d H:mm:ss").create()
    }

    private object DateFormatterHolder {
        val FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss", Locale.US)
                .withZone(ZoneId.systemDefault())
    }
}
