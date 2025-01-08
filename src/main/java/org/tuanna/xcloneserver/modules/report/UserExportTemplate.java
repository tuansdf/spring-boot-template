package org.tuanna.xcloneserver.modules.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExportTemplate implements ExportTemplate<UserDTO> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Status", "Created By", "Updated By", "Created At", "Updated At");
    private static final Function<UserDTO, List<Object>> ROW_DATA_EXTRACTOR_FORMATTED = user -> Arrays.asList(
            ConversionUtils.toString(user.getId()),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getStatus(),
            ConversionUtils.toString(user.getCreatedBy()),
            ConversionUtils.toString(user.getUpdatedBy()),
            DateUtils.toString(user.getCreatedAt(), DateUtils.Formatter.DATE_TIME_BE),
            DateUtils.toString(user.getUpdatedAt(), DateUtils.Formatter.DATE_TIME_BE));
    private static final Function<UserDTO, List<Object>> ROW_DATA_EXTRACTOR = user -> Arrays.asList(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getStatus(),
            user.getCreatedBy(),
            user.getUpdatedBy(),
            user.getCreatedAt(),
            user.getUpdatedAt());

    private List<UserDTO> body;

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public List<UserDTO> getBody() {
        return body;
    }

    @Override
    public Function<UserDTO, List<Object>> getRowDataExtractor(boolean formatAsString) {
        return formatAsString ? ROW_DATA_EXTRACTOR_FORMATTED : ROW_DATA_EXTRACTOR;
    }

    @Override
    public List<CellStyle> getColStyles(Workbook workbook) {
        CellStyle dateCellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat(DateUtils.Format.DATE_TIME_FE));
        return Arrays.asList(null, null, null, null, null, null, null, dateCellStyle, dateCellStyle);
    }

}
