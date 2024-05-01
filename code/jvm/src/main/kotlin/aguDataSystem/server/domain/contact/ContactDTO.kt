package aguDataSystem.server.domain.contact

data class ContactDTO(
	val name: String,
	val phone: String,
	val type: String
) {
	fun toContact() = Contact(
		name = this.name,
		phone = this.phone,
		type = this.type.toContactType()
	)
}