package editor;

import java.awt.event.KeyEvent;

public interface BaseControllerListener {
	public void receivedHotkey(BaseController.EKeyBinding binding, int keyEventCode);
}
