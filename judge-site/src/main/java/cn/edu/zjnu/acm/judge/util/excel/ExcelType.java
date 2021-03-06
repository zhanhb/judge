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
package cn.edu.zjnu.acm.judge.util.excel;

import cn.edu.zjnu.acm.judge.util.CustomMediaType;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.MediaType;

/**
 *
 * @author zhanhb
 */
@RequiredArgsConstructor
public enum ExcelType {

    XLS(CustomMediaType.XLS, HSSFWorkbook::new),
    XLSX(CustomMediaType.XLSX, SXSSFWorkbook::new);

    private final MediaType mediaType;
    private final Supplier<Workbook> supplier;

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return name().toLowerCase();
    }

    Workbook createWorkBook() {
        return supplier.get();
    }

}
