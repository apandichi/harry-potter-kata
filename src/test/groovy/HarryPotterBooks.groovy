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

	def "a set of copies of the same book are not eligible for discount"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")
		def bookThree = newBook(8, "Philosopher's Stone")

		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookTwo, bookThree])

		then:
		!booksAreEligibleForDiscount
	}

	def "a set of different books are eligible for discount"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Chamber of Secrets")

		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookTwo, bookThree])

		then:
		booksAreEligibleForDiscount
	}

	def "a set of no books has no discount"() {
		given:
		def books = []

		when:
		def discount = getDiscount(books)

		then:
		discount == 0
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

	def "a set of two identical books are not discounted"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Philosopher's Stone")
		def books = [bookOne, bookTwo]

		when:
		def discount = getDiscount(books)

		then:
		discount == 0
	}

	def "a set of two different books are discounted with 5%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def books = [bookOne, bookTwo]

		when:
		def discount = getDiscount(books)

		then:
		discount == 5
	}

	def "a set of three different books are discounted with 10%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Prisoner of Azkaban")
		def books = [bookOne, bookTwo, bookThree]

		when:
		def discount = getDiscount(books)

		then:
		discount == 10
	}

	def "a set of four different books are discounted with 20%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Prisoner of Azkaban")
		def bookFour = newBook(8, "Goblet of Fire")
		def books = [bookOne, bookTwo, bookThree, bookFour]

		when:
		def discount = getDiscount(books)

		then:
		discount == 20
	}

	def "a set of five different books are discounted with 25%"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Prisoner of Azkaban")
		def bookFour = newBook(8, "Goblet of Fire")
		def bookFive = newBook(8, "Order of the Phoenix")
		def books = [bookOne, bookTwo, bookThree, bookFour, bookFive]

		when:
		def discount = getDiscount(books)

		then:
		discount == 25
	}

	def getDiscount(books) {
		def discounts = [0, 0, 5, 10, 20, 25]
		return booksAreEligibleForDiscount(books) ? discounts[books.size] : 0
	}

	def booksAreEligibleForDiscount(books) {
		def booksAreTheSame = books.unique(false).size == 1
		return !booksAreTheSame
	}

	def booksAreDifferent(bookOne, bookTwo) {
		return bookOne != bookTwo
	}
}