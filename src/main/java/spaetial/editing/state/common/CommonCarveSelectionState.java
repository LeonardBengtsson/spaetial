package spaetial.editing.state.common;

import spaetial.editing.state.EditingStateType;

public record CommonCarveSelectionState() implements CommonEditingState {
    @Override
    public EditingStateType getType() { return EditingStateType.CARVE_SELECTION; }
}
