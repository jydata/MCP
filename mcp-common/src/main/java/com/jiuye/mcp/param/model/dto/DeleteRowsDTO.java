package com.jiuye.mcp.param.model.dto;

import com.jiuye.mcp.param.enums.DatabaseEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jepson
 */
public class DeleteRowsDTO extends EventBaseDTO {
    private static final long serialVersionUID = -4305297516487395336L;
    private List<Map<String, Serializable>> rowMaps;

    public DeleteRowsDTO() {
    }

    public DeleteRowsDTO(EventBaseDTO eventBaseDTO, List<Map<String, Serializable>> rowMaps) {
        super(eventBaseDTO);
        super.setEventType(DatabaseEvent.DELETE_ROWS);
        this.rowMaps = rowMaps;
    }

    public List<Map<String, Serializable>> getRowMaps() {
        return rowMaps;
    }

    public void setRowMaps(List<Map<String, Serializable>> rowMaps) {
        this.rowMaps = rowMaps;
    }

    @Override
    public String toString() {
        return "DeleteRowsDTO{" +
                "rowMaps=" + rowMaps +
                "} " + super.toString();
    }
}
