package org.tuanna.xcloneserver.dtos;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ExcelCellListConfig {

    @Builder.Default
    private int row = 0;
    @Builder.Default
    private int col = 0;

    private List<?> value; // single value (string/int/date) or a list of objects
    private Map<String, Integer> mapper; // map each value to column

}
