package aguDataSystem.server.domain

import java.time.LocalDateTime

/**
 * Represents a load to be done in some [AGU].
 *
 * @property reference the reference given on the WLP (Weekly Load Planing)
 * @property companyType the company that preforms de load
 * @property timeStamp the timestamp that the load is programmed
 * @property amount the amount of Gas in the load
 * @property isCompleted if the load was delivered
 */
class Load(
	val reference: Int,
	val companyType: CompanyType,
	val timeStamp: LocalDateTime,
	val amount: Int,
	val isCompleted: Boolean,
)