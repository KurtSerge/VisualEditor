package editor;

public interface BaseControllerListener {
	public boolean receivedHotkey(BaseController baseController, BaseController.EKeyBinding binding, int keyEventCode);
}
