package ua.zxz.multydbsysytem.web.payload;

import lombok.Data;

@Data
public class RenameColumnPayload {

    private String oldName;
    private String newName;
}
