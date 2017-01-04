import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll


class HarryPotterBooks extends Specification {

	static bookOne = newBook(8, "Philosopher's Stone")
	static bookTwo = newBook(8, "Chamber of Secrets")
	static bookThree = newBook(8, "Prisoner of Azkaban")
	static bookFour = newBook(8, "Goblet of Fire")
	static bookFive = newBook(8, "Order of the Phoenix")

	def "a book has a specific price"() {
		given:
		def book = bookOne

		when:
		def bookPrice = book.price

		then:
		bookPrice == 8
	}

	private static newBook(bookPrice, bookName) {
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
		books                             | booksAreDifferentExpected
		[]                                | true
		groupOfBooks(1)                   | true
		groupOfBooks(2)                   | true
		groupOfBooks(1) + groupOfBooks(1) | false
		allBooks()                        | true
		[bookOne, bookTwo, bookOne]       | false
	}

	def "a set of copies of the same book are not eligible for discount"() {
		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookOne, bookOne])

		then:
		!booksAreEligibleForDiscount
	}

	def "a set of different books are eligible for discount"() {
		when:
		def booksAreEligibleForDiscount = booksAreEligibleForDiscount([bookOne, bookTwo, bookTwo])

		then:
		booksAreEligibleForDiscount
	}

	def "a set of two identical books are not discounted"() {
		given:
		def books = [bookOne, bookOne]

		when:
		def discount = getDiscount(books)

		then:
		discount == 0
	}

	@Unroll
	def "a set of #numberOfBooks different books are discounted with #discount%"() {
		given:
		def books = groupOfBooks(numberOfBooks)

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
	def "applying discount of #discount% on full price of #price results in discounted price of #discountedPriceExpected"() {
		when:
		def discountedPrice = getDiscountedPrice(discount, fullPrice)

		then:
		discountedPrice == discountedPriceExpected

		where:
		discount | fullPrice | discountedPriceExpected
		0        | 10        | 10
		5        | 10        | 9.5
		10       | 10        | 9
		20       | 10        | 8
		25       | 10        | 7.5
		0        | 8         | 8
		5        | 8         | 7.6
		10       | 8         | 7.2
		20       | 8         | 6.4
		25       | 8         | 6
	}

	@Unroll
	def "the set of books #books has a full price 0f #expectedFullPrice"() {
		when:
		def fullPrice = getFullPrice(books)

		then:
		fullPrice == expectedFullPrice

		where:
		books                             | expectedFullPrice
		[]                                | 0
		groupOfBooks(0)                   | 0
		groupOfBooks(1)                   | 8
		groupOfBooks(1) + groupOfBooks(1) | 16
		groupOfBooks(2)                   | 16
		groupOfBooks(2) + groupOfBooks(1) | 24
		groupOfBooks(5)                   | 40
		allBooks()                        | 40
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
		books                                                    | expectedGroupsOfBooks                                          | groupsSizeExpected
		[]                                                       | []                                                             | 0
		groupOfBooks(1)                                          | [[newBook(8, "Philosopher's Stone")]]                          | 1
		groupOfBooks(2)                                          | [groupOfBooks(2)]                                              | 1
		groupOfBooks(1) + groupOfBooks(1)                        | [groupOfBooks(1), groupOfBooks(1)]                             | 2
		groupOfBooks(1) + groupOfBooks(2)                        | [groupOfBooks(2), groupOfBooks(1)]                             | 2
		[bookOne, bookOne, bookTwo, bookOne, bookThree, bookTwo] | [[bookOne, bookTwo, bookThree], [bookOne, bookTwo], [bookOne]] | 3
	}

	@Ignore
	def "a specific basket of books costs 51.2 after discount"() {
		given:
		def basketOfBooks = copiesOfBook(2, 1) + copiesOfBook(2, 2) + copiesOfBook(2, 3) + copiesOfBook(1, 4) + copiesOfBook(1, 5)

		when:
		def discountedPrice = getDiscountedPrice(basketOfBooks)

		then:
		discountedPrice == 51.2
	}

	@Unroll
	def "get copies of the same book"() {
		when:
		def books = copiesOfBook(copies, bookIndex)

		then:
		books == expectedBooks

		where:
		copies | bookIndex | expectedBooks
		0      | 0         | []
		0      | 1         | []
		1      | 1         | [bookOne]
		1      | 2         | [bookTwo]
		2      | 2         | [bookTwo, bookTwo]
	}

	def copiesOfBook(copies, bookIndex) {
		copies > 0 ? (1..copies).collect {
			[allBooks().get(bookIndex - 1)]
		}.sum() : []
	}

	def groupBooks(List books) {
		books.inject([], { result, book ->
			List foundGroup = result.find { booksAreDifferent(it + [book]) } ?: {
				def newGroup = []
				result.add(newGroup)
				return newGroup
			}.call()
			foundGroup.add(book)
			return result
		})
	}

	def getFullPrice(books) {
		return books.inject(0, { acc, val -> acc + val.price })
	}

	def getDiscountedPrice(discount, fullPrice) {
		return fullPrice - fullPrice * discount / 100
	}

	def "four books, of which 3 are different, get a 10% discount for the set of 3, but the fourth book still costs 8 EUR"() {
		given:
		def books = groupOfBooks(3) + groupOfBooks(1)

		when:
		def price = getDiscountedPrice(books)

		then:
		price == 29.6
	}

	def getDiscountedPrice(books) {
		def groupsOfBooks = groupBooks(books)
		return groupsOfBooks.collect {
			def discount = getDiscount(it)
			def price = getFullPrice(it)
			return getDiscountedPrice(discount, price)
		}.sum()
	}

	def groupOfBooks(numberOfBooks) {
		return allBooks().subList(0, numberOfBooks)
	}

	private allBooks() {
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
		return books.groupBy { it.name }.size() == books.size()
	}

	def booksAreDifferent(bookOne, bookTwo) {
		return bookOne != bookTwo
	}
}