package editor;

import java.awt.event.KeyEvent;

public interface BaseControllerListener {
	public void receivedHotkey(BaseController baseController, BaseController.EKeyBinding binding, int keyEventCode);
}
