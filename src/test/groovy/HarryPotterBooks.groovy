import spock.lang.Specification


class HarryPotterBooks extends Specification {

	def "a book has a specific price"(){
		given:
		def book = [price: 8]

		when:
		def bookPrice = book.price

		then:
		bookPrice == 8
	}
}