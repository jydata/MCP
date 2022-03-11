package com.jiuye.mcp.param.model.dto;

import com.jiuye.mcp.param.enums.DatabaseEvent;

import java.util.List;

/**
 * @author jepson
 */
public class UpdateRowsDTO extends EventBaseDTO {

    private static final long serialVersionUID = 5076165787262824629L;

    private List<UpdateRow> rows;

    public UpdateRowsDTO() {
    }

    public UpdateRowsDTO(EventBaseDTO eventBaseDTO, List<UpdateRow> rows) {
        super(eventBaseDTO);
        super.setEventType(DatabaseEvent.UPDATE_ROWS);
        this.rows = rows;
    }

    public List<UpdateRow> getRows() {
        return rows;
    }

    public void setRows(List<UpdateRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "UpdateRowsDTO{" +
                "rows=" + rows +
                "} " + super.toString();
    }
}
