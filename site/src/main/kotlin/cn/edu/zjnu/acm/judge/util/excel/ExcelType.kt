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

import cn.edu.zjnu.acm.judge.util.CustomMediaType
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.http.MediaType

/**
 *
 * @author zhanhb
 */
enum class ExcelType(
        val mediaType: MediaType,
        private val creator: () -> Workbook
) {

    XLS(CustomMediaType.XLS,  { HSSFWorkbook() }),
    XLSX(CustomMediaType.XLSX,  { SXSSFWorkbook() });

    val extension: String
        get() = name.toLowerCase()

    internal fun createWorkBook(): Workbook {
        return creator()
    }

}
