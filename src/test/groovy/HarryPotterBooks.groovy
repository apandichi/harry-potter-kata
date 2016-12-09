import spock.lang.Specification


class HarryPotterBooks extends Specification {

	def "a book has a specific price"() {
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

	def "a set of copies of the same book are eligible for discount"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")
		def bookThree = newBook(8, "Philosopher's Stone")

		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookTwo, bookThree])

		then:
		booksAreEligibleForDiscount
	}

	def "a set of different books are not eligible for discount"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Chamber of Secrets")

		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookTwo, bookThree])

		then:
		!booksAreEligibleForDiscount
	}

	def "one book has no discount"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def books = [bookOne]

		when:
		def discount = getDiscount(books)

		then:
		discount == 0
	}

	def "a set of two identical books are discounted with 5%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")
		def books = [bookOne, bookTwo]

		when:
		def discount = getDiscount(books)

		then:
		discount == 5
	}

	def "a set of three identical books are discounted with 10%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")
		def bookThree = newBook(8, "Philosopher's Stone")
		def books = [bookOne, bookTwo, bookThree]

		when:
		def discount = getDiscount(books)

		then:
		discount == 10
	}

	def getDiscount(books) {
		def discounts = [0, 0, 5, 10]
		return discounts[books.size]
	}

	def booksAreEligibleForDiscount(books) {
		def booksAreTheSame = books.unique(false).size == 1
		return booksAreTheSame
	}

	def booksAreDifferent(bookOne, bookTwo) {
		return bookOne != bookTwo
	}
}