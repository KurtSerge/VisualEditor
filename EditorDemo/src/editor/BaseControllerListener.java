package editor;

public interface BaseControllerListener {
	public void receivedHotkey(BaseController baseController, BaseController.EKeyBinding binding, int keyEventCode);
}
