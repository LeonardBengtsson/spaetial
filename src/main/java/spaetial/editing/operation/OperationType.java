package spaetial.editing.operation;

import spaetial.Spaetial;
import spaetial.server.permissions.PermissionLevel;
import spaetial.util.Translatable;

public enum OperationType implements Translatable {
    COPY("copy", PermissionLevel.EDITOR),
    CLONE("clone", PermissionLevel.EDITOR),
    LINE_STACK("line_stack", PermissionLevel.EDITOR),
    VOLUME_STACK("volume_stack", PermissionLevel.EDITOR),
    REPLACE("replace", PermissionLevel.EDITOR),
    COMPLETE_SCHEMATIC("complete_schematic", PermissionLevel.EDITOR);

    public final String name;
    public final PermissionLevel permissionLevel;

    OperationType(String name, PermissionLevel permissionLevel) {
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    @Override
    public String getTranslationKey() {
        return Spaetial.translationKey(null, "operation", name, "name");
    }
}
