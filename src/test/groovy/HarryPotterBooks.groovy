import spock.lang.Specification


class HarryPotterBooks extends Specification {

	def "a book has a specific price"(){
		given:
		def book = newBook(8, "Philosopher's Stone")

		when:
		def bookPrice = book.price

		then:
		bookPrice == 8
	}

	private newBook(bookPrice, bookName) {
		[price: bookPrice, name: bookName]
	}

	def "checks that two different books are different"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")

		when:
		def booksAreDifferent = booksAreDifferent(bookOne, bookTwo)

		then:
		booksAreDifferent
	}

	def "check that two copies of the same book are equal"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")

		when:
		def booksAreDifferent = booksAreDifferent(bookOne, bookTwo)

		then:
		!booksAreDifferent
	}

	def booksAreDifferent(bookOne, bookTwo) {
		return bookOne != bookTwo
	}
}