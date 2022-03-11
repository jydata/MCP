package com.jiuye.mcp.param.model.dto;

import com.jiuye.mcp.param.enums.DatabaseEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jepson
 */
public class InsertRowsDTO extends EventBaseDTO {

    private static final long serialVersionUID = -6906838707044587433L;

    private List<Map<String, Serializable>> rowMaps;

    public InsertRowsDTO() {
    }

    public InsertRowsDTO(EventBaseDTO eventBaseDTO, List<Map<String, Serializable>> rowMaps) {
        super(eventBaseDTO);
        super.setEventType(DatabaseEvent.INSERT_ROWS);
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
        return "InsertRowsDTO{" +
                "rowMaps=" + rowMaps +
                "} " + super.toString();
    }
}
