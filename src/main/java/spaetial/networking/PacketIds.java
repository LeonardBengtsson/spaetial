package spaetial.networking;

import net.minecraft.util.Identifier;
import spaetial.Spaetial;

@Deprecated
public class PacketIds {
    public static class C2S {
        // client to server

        // request_undo:
        public static final Identifier REQUEST_UNDO = Spaetial.id("request_undo");

        // request_redo:
        public static final Identifier REQUEST_REDO = Spaetial.id("request_redo");

        // request_cut_operation:
        // Selection selection
        // RegistryKey<World> dimension
        public static final Identifier REQUEST_CUT_OPERATION = Spaetial.id("request_cut_operation");

        // request_move_operation:
        // Selection selection
        // RegistryKey<World> dimension
        // BlockPos delta
        public static final Identifier REQUEST_MOVE_OPERATION = Spaetial.id("request_move_operation");

        // request_copy_operation:
        // Selection selection
        // RegistryKey<World> dimension
        // BlockPos delta
        public static final Identifier REQUEST_COPY_OPERATION = Spaetial.id("request_copy_operation");

        // request_line_stack_operation:
        // Selection selection
        // RegistryKey<World> dimension
        // BlockPos delta
        // int stackSize
        // BlockPos spacing
        public static final Identifier REQUEST_LINE_STACK_OPERATION = Spaetial.id("request_line_stack_operation");

        // request_volume_stack_operation:
        // Selection selection
        // RegistryKey<World> dimension
        // BlockPos delta
        // Vec3i stackSize
        // BlockPos spacing
        public static final Identifier REQUEST_VOLUME_STACK_OPERATION = Spaetial.id("request_volume_stack_operation");

        // config_update:
        public static final Identifier CONFIG_UPDATE = Spaetial.id("config_update");

        // state_update:
        // EditingState state
        public static final Identifier STATE_UPDATE = Spaetial.id("state_update");

        // send_addressed_state_update:
        // UUID addressee
        // EditingState state
        public static final Identifier ADDRESSED_STATE_UPDATE = Spaetial.id("addressed_state_update");

        // request_multiplayer_cache_update:
        public static final Identifier REQUEST_MULTIPLAYER_CACHE_UPDATE = Spaetial.id("request_multiplayer_cache_update");

        // request_region:
        // UUID requestId
        // Selection selection
        // RegistryKey<World> dimension
        public static final Identifier REQUEST_REGION = Spaetial.id("request_region");

        // request_collaborative_blueprint_region:
        // UUID requestId
        // UUID blueprintId
        public static final Identifier REQUEST_COLLABORATIVE_BLUEPRINT_REGION = Spaetial.id("request_collaborative_blueprint_region");

        // upload_blueprint_head:
        // UUID transmissionId
        // UUID blueprintId
        // BlockPos minPos
        // RegistryKey<World> dimension
        // BlueprintMetadata metadata
        public static final Identifier UPLOAD_BLUEPRINT_HEAD = Spaetial.id("upload_blueprint_head");

        // upload_blueprint_part:
        // UUID transmissionId
        // int totalPacketCount
        // int packetIndex
        // byte[] data
        public static final Identifier UPLOAD_BLUEPRINT_PART = Spaetial.id("upload_blueprint_part");

        // paste_blueprint:
        // UUID id
        // BlockPos minPos
        // RegistryKey<World> sourceDim
        public static final Identifier PASTE_BLUEPRINT = Spaetial.id("paste_blueprint");

        // place_blueprint:
        // UUID id
        public static final Identifier PLACE_BLUEPRINT = Spaetial.id("place_blueprint");

        // move_blueprint:
        // UUID id
        // BlockPos minPos
        // RegistryKey<World> dimension
        public static final Identifier MOVE_BLUEPRINT = Spaetial.id("move_blueprint");

        // toggle_collab_blueprint:
        // UUID blueprintId
        // boolean toggle
        public static final Identifier TOGGLE_COLLAB_BLUEPRINT = Spaetial.id("toggle_collab_blueprint");

        // toggle_joined_collab_blueprint:
        // UUID blueprintId
        // boolean toggle
        public static final Identifier TOGGLE_JOINED_COLLAB_BLUEPRINT = Spaetial.id("toggle_joined_collab_blueprint");
    }
    public static class S2C {
        // server to client

        // multiplayer_cache_update:
        // boolean type
        // UUID id
        // [type = false]
        //     EditingState state
        public static final Identifier MULTIPLAYER_CACHE_UPDATE = Spaetial.id("multiplayer_cache_update");

        // request_addressed_state_update:
        // UUID addressee
        public static final Identifier REQUEST_ADDRESSED_STATE_UPDATE = Spaetial.id("request_addressed_state_update");

        // operation_result:
        public static final Identifier OPERATION_RESULT = Spaetial.id("operation_result");

        // send_region_part:
        // UUID requestId
        // int totalPacketCount
        // int packetIndex
        // byte[] data
        public static final Identifier SEND_REGION_PART = Spaetial.id("send_region_part");

        // blueprint_collab_toggled
        // UUID blueprintId
        // boolean toggle
        // [toggle = true]
        //     ClientCollaborativeBlueprintInfo info
        public static final Identifier BLUEPRINT_COLLAB_TOGGLED = Spaetial.id("blueprint_collab_toggled");
    }
}
