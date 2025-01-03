package org.tuanna.xcloneserver.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ExcelCellConfig {

    @Builder.Default
    private int row = 0;
    @Builder.Default
    private int rowMergedTo = -1;
    @Builder.Default
    private int col = 0;
    @Builder.Default
    private int colMergedTo = -1;

    private Object value; // single value (string/int/date) or a list of objects

    public ExcelCellConfig(int row, int col, Object value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

}
