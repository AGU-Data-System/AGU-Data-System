package aguDataSystem.server.domain

class AGUDomain {

    companion object {

        private val cuiRegex = Regex("^PT[0-9]{16}[A-Z]{2}$")
        private val phoneRegex = Regex("^[0-9]{9}$")
    }

    /**
     * Checks if the given CUI is valid
     * @param cui the CUI to check
     * @return true if the CUI is valid, false otherwise
     */
    fun isCUIValid(cui: String): Boolean = cuiRegex.matches(cui)

    /**
     * Checks if the given phone number is valid
     * @param phone the phone number to check
     * @return true if the phone number is valid, false otherwise
     */
    fun isPhoneValid(phone: String): Boolean = phoneRegex.matches(phone)

    /**
     * Checks if a percentage is valid
     * @param percentage the percentage to check
     * @return true if the percentage is valid, false otherwise
     */
    fun isPercentageValid(percentage: Int): Boolean = percentage in 0..100

    /**
     * Checks if a Latitude is valid
     * @param latitude the latitude to check
     * @return true if the latitude is valid, false otherwise
     */
    fun isLatitudeValid(latitude: Double): Boolean = latitude in -90.0..90.0

    /**
     * Checks if a Longitude is valid
     * @param longitude the longitude to check
     * @return true if the longitude is valid, false otherwise
     */
    fun isLongitudeValid(longitude: Double): Boolean = longitude in -180.0..180.0

    /**
     * Checks if there's at least one tank in the AGU
     * @param tanks the tanks to check
     * @return true if there's at least one tank, false otherwise
     */
    fun isTanksValid(tanks: List<Tank>): Boolean = tanks.isNotEmpty()
}