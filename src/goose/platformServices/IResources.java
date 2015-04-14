package goose.platformServices;

public interface IResources {
	/*
	 * Returns the percentage of battery available
	 */
	public int percentageBatteryLevel();
	
	/*
	 * Returns the percentage of available memory
	 */
	public int percentageAvailableMemory();
	
	/*
	 * Returns the available memory in bytes.
	 */
	public long totalAvailableMemory();
}
