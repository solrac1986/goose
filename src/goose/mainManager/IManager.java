package goose.mainManager;

import goose.exceptions.*;

public interface IManager {
	public void startManager() throws GooseException;
	public void stopManager() throws GooseException;
	public void pauseManager() throws GooseException;
	public void restartManager() throws GooseException;
}
