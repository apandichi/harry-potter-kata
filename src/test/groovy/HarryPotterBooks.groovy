import spock.lang.Ignore
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

	def "a set of books are different"() {
		when:
		def booksAreDifferent = booksAreDifferent(books)

		then:
		booksAreDifferent == booksAreDifferentExpected

		where:
		books                         | booksAreDifferentExpected
		[]                            | true
		firstBooks(1)                 | true
		firstBooks(2)                 | true
		firstBooks(1) + firstBooks(1) | false
		allBooks()                    | true
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

	@Unroll
	def "apply discount of #discount% for price of #price results in discounted price of #discountedPriceExpected"() {
		when:
		def discountedPrice = applyDiscountForPrice(discount, price)

		then:
		discountedPrice == discountedPriceExpected

		where:
		discount | price | discountedPriceExpected
		0        | 10    | 10
		5        | 10    | 9.5
		10       | 10    | 9
		20       | 10    | 8
		25       | 10    | 7.5
		0        | 8     | 8
		5        | 8     | 7.6
		10       | 8     | 7.2
		20       | 8     | 6.4
		25       | 8     | 6
	}

	@Unroll
	def "the set of books #books has a full price 0f #expectedFullPrice"() {
		when:
		def fullPrice = getFullPrice(books)

		then:
		fullPrice == expectedFullPrice

		where:
		books                         | expectedFullPrice
		[]                            | 0
		firstBooks(0)                 | 0
		firstBooks(1)                 | 8
		firstBooks(1) + firstBooks(1) | 16
		firstBooks(2)                 | 16
		firstBooks(2) + firstBooks(1) | 24
		firstBooks(5)                 | 40
		allBooks()                    | 40
	}

	@Unroll
	def "group a set of various books into a list of groups, each containing different books"() {
		when:
		def groupsOfBooks = groupBooks(books)

		then:
		groupsOfBooks == expectedGroupsOfBooks
		groupsOfBooks.size() == groupsSizeExpected
		groupsOfBooks.size() < 1 || groupsOfBooks.each { assert booksAreDifferent(it) }

		where:
		books                               | expectedGroupsOfBooks                 | groupsSizeExpected
		[]                                  | []                                    | 0
		firstBooks(1)                       | [[newBook(8, "Philosopher's Stone")]] | 1
		firstBooks(2)                       | [firstBooks(2)]                       | 1
		firstBooks(1) + firstBooks(1)       | [firstBooks(1), firstBooks(1)]        | 2
		firstBooks(1) + firstBooks(2)       | [firstBooks(2), firstBooks(1)]        | 2
	}

	def groupBooks(List books) {
		books.inject([], { result, book ->
			println("inject book $book")
			List foundGroup = result.find { booksAreDifferent(it + [book]) } ?: {
				println('newGroup')
				def newGroup = []
				result.add(newGroup)
				return newGroup
			}.call()
			foundGroup.add(book)
			println("foundGroup $foundGroup")
			println("------------")
			return result
		})
	}

	def getFullPrice(books) {
		return books.inject(0, { acc, val -> acc + val.price })
	}

	def applyDiscountForPrice(discount, price) {
		return price - price * discount / 100
	}

	@Ignore
	def "four books, of which 3 are different, get a 10% discount for the set of 3, but the fourth book still costs 8 EUR"() {
		given:
		def books = firstBooks(3) + firstBooks(1)

		when:
		def price = getDiscountedPrice(books)

		then:
		price == applyDiscountForPrice(10, (8 * 3)) + 8
	}

	def getDiscountedPrice(books) {
		def groupsOfBooks = groupBooks(books)
		return groupsOfBooks.collect {
			def discount = getDiscount(it)
			def price = getFullPrice(it)
			return applyDiscountForPrice(discount, price)
		}
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

	def booksAreDifferent(books) {
		def booksAreTheSame = books.unique(false).size == 1
		return books.size < 2 || !booksAreTheSame
	}

	def booksAreDifferent(bookOne, bookTwo) {
		return bookOne != bookTwo
	}
}