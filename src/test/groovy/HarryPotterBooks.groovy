import spock.lang.Specification
import spock.lang.Unroll


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

	def "two different books are different"() {
		given:
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")

		when:
		def booksAreDifferent = booksAreDifferent(bookOne, bookTwo)

		then:
		booksAreDifferent
	}

	def "two copies of the same book are equal"() {
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

	@Unroll
	def "a set of #numberOfBooks different books are discounted with #discount%"() {
		given:
		def books = firstBooks(numberOfBooks)

		when:
		def discountResult = getDiscount(books)

		then:
		discountResult == discount

		where:
		numberOfBooks | discount
		0             | 0
		1             | 0
		2             | 5
		3             | 10
		4             | 20
		5             | 25
	}

	def firstBooks(numberOfBooks) {
		return allBooks().subList(0, numberOfBooks)
	}

	private allBooks() {
		def bookOne = newBook(8, "Philosopher's Stone")
		def bookTwo = newBook(8, "Chamber of Secrets")
		def bookThree = newBook(8, "Prisoner of Azkaban")
		def bookFour = newBook(8, "Goblet of Fire")
		def bookFive = newBook(8, "Order of the Phoenix")
		return [bookOne, bookTwo, bookThree, bookFour, bookFive]
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