package aguDataSystem.server.service

/**
 * Enum class to store the URLs of the data providers
 * TODO Domain? Service?
 */
enum class DataProviders(val providerUrl: String) {
	DNO("http://localhost:8080/dno"),
	Temperature("http://localhost:8080/temperature"),
	AGUSonarGas("http://localhost:8080/aguSonarGas"),
	AGUFloeno("http://localhost:8080/aguFloeno"),
	Location("http://localhost:8080/location");

	/**
	 * Create the URL for the [Temperature] data provider
	 *
	 * @param location the location to get the Temperature data for
	 * @return the URL for the Temperature data provider
	 */
	fun createTemperatureURL(location: String) = "${Temperature.providerUrl}/$location"
}