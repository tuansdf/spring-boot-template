package org.tuanna.xcloneserver.modules.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestUserExportTemplate implements ExportTemplate<TestUser> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Address", "Street", "City", "Country", "Created At", "Updated At");
    private static final Function<TestUser, List<Object>> ROW_DATA_EXTRACTOR_FORMATTED = user -> Arrays.asList(
            user.getId().toString(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getAddress(),
            user.getStreet(),
            user.getCity(),
            user.getCountry(),
            DateUtils.toFormat(user.getCreatedAt(), DateUtils.Formatter.DATE_TIME_BE),
            DateUtils.toFormat(user.getUpdatedAt(), DateUtils.Formatter.DATE_TIME_BE));
    private static final Function<TestUser, List<Object>> ROW_DATA_EXTRACTOR = user -> Arrays.asList(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getAddress(),
            user.getStreet(),
            user.getCity(),
            user.getCountry(),
            user.getCreatedAt(),
            user.getUpdatedAt());
    private static final Function<Workbook, List<CellStyle>> ROW_STYLE_EXTRACTOR = workbook -> {
        CellStyle dateCellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat(DateUtils.Format.DATE_TIME_FE));
        return Arrays.asList(null, null, null, null, null, null, null, null, dateCellStyle, dateCellStyle);
    };

    private List<TestUser> body;

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public List<TestUser> getBody() {
        return body;
    }

    @Override
    public Function<TestUser, List<Object>> getRowDataExtractor(boolean formatAsString) {
        return formatAsString ? ROW_DATA_EXTRACTOR_FORMATTED : ROW_DATA_EXTRACTOR;
    }

    @Override
    public Function<Workbook, List<CellStyle>> getRowStyleExtractor() {
        return ROW_STYLE_EXTRACTOR;
    }

}
