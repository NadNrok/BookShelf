package com.limethecoder.util.editor;


import com.limethecoder.data.domain.Role;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class RoleEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        setValue(new Role(text));
    }
}
