#ifndef BUCKET_STORAGE_HPP
#define BUCKET_STORAGE_HPP

#include <stdexcept>

// VECTOR

template< typename T >
class vector
{
  private:
	T* arr;
	using size_type = size_t;
	size_type capacit;
	size_type sizee;

  public:
	vector(size_type initial_capacity = 10);

	~vector();

	void swap(vector& other) noexcept;

	vector(const vector& other);

	vector(vector&& other) noexcept;

	vector& operator=(const vector& other);
	vector& operator=(vector&& other) noexcept;

	template< typename U >
	void push(U&& el);

	size_type size() const noexcept;

	size_type capacity() const noexcept;

	T& operator[](size_type idx);
	const T& operator[](size_type idx) const;
};

template< typename T >
vector< T >::vector(size_type initial_capacity) : capacit(initial_capacity), sizee(0)
{
	if (capacit > 0)
	{
		arr = static_cast< T* >(::operator new(capacit * sizeof(T)));
	}
	else
	{
		arr = nullptr;
	}
}

template< typename T >
vector< T >::~vector()
{
	for (size_t idx = 0; idx < sizee; ++idx)
	{
		arr[idx].~T();
	}
	::operator delete(arr);
}

template< typename T >
void vector< T >::swap(vector& other) noexcept
{
	using std::swap;
	swap(arr, other.arr);
	swap(capacit, other.capacit);
	swap(sizee, other.sizee);
}

template< typename T >
vector< T >::vector(const vector& other) : capacit(other.capacit), sizee(other.sizee)
{
	if (capacit > 0)
	{
		arr = static_cast< T* >(::operator new(capacit * sizeof(T)));
		for (size_t idx = 0; idx < sizee; ++idx)
		{
			new (&arr[idx]) T(other.arr[idx]);
		}
	}
	else
	{
		arr = nullptr;
	}
}

template< typename T >
vector< T >::vector(vector&& other) noexcept : arr(other.arr), capacit(other.capacit), sizee(other.sizee)
{
	other.arr = nullptr;
	other.capacit = 0;
	other.sizee = 0;
}

template< typename T >
vector< T >& vector< T >::operator=(const vector& other)
{
	if (this != &other)
	{
		vector temp(other);
		swap(temp);
	}
	return *this;
}

template< typename T >
vector< T >& vector< T >::operator=(vector&& other) noexcept
{
	if (this != &other)
	{
		for (size_t idx = 0; idx < sizee; ++idx)
		{
			arr[idx].~T();
		}
		::operator delete(arr);

		swap(other);

		other.arr = nullptr;
		other.capacit = 0;
		other.sizee = 0;
	}
	return *this;
}

template< typename T >
template< typename U >
void vector< T >::push(U&& el)
{
	if (sizee == capacit)
	{
		size_type new_capacity = capacit > 0 ? capacit * 2 : 1;
		T* temp = static_cast< T* >(::operator new(new_capacity * sizeof(T)));

		for (size_t idx = 0; idx < sizee; ++idx)
		{
			new (&temp[idx]) T(std::move(arr[idx]));
		}
		::operator delete(arr);

		arr = temp;
		capacit = new_capacity;
	}

	// perfect forwarding
	new (&arr[sizee]) T(std::forward< U >(el));
	sizee++;
}

template< typename T >
size_t vector< T >::size() const noexcept
{
	return sizee;
}

template< typename T >
size_t vector< T >::capacity() const noexcept
{
	return capacit;
}

template< typename T >
T& vector< T >::operator[](size_t idx)
{
	if (idx < sizee)
	{
		return arr[idx];
	}
	throw std::out_of_range("vector idx is out of range");
}

template< typename T >
const T& vector< T >::operator[](size_t idx) const
{
	if (idx < sizee)
	{
		return arr[idx];
	}
	throw std::out_of_range("vector idx is out of range");
}

template< typename T, typename value_type >
class Iterator;

// BLOCK

template< typename T >
class Block
{
  private:
	T* data;
	size_t* distance_to_next_free;

  private:
	void fix_distances() noexcept;

	friend class Iterator< T, T >;
	friend class Iterator< T, const T >;

  public:
	using size_type = size_t;
	size_t capacity;
	Block(size_t capacity);
	~Block();

	// Rule of Five
	Block(const Block& other);
	Block(Block&& other) noexcept;
	Block& operator=(Block other);
	void swap(Block& other) noexcept;

	size_t insert(const T& value);	  // insert as left as possible, return insertion index
	size_t insert(T&& value);
	size_t erase(size_t index);	   // erase at index, return next existing element
	// index or 0 if not possible
	bool is_full() const noexcept;
	bool is_empty() const noexcept;

	T& operator[](size_t idx);
};

template< typename T >
Block< T >::Block(size_t capacity) : capacity(capacity)
{
	if (capacity > 0)
	{
		data = static_cast< T* >(::operator new(capacity * sizeof(T)));
		distance_to_next_free = new size_t[capacity];
		std::fill_n(distance_to_next_free, capacity, 0);
	}
	else
	{
		data = nullptr;
		distance_to_next_free = nullptr;
	}
}

template< typename T >
Block< T >::~Block()
{
	for (size_t i = 0; i < capacity; ++i)
	{
		if (distance_to_next_free[i] != 0)
		{
			data[i].~T();
		}
	}
	::operator delete(data);
	delete[] distance_to_next_free;
}

template< typename T >
Block< T >::Block(const Block& other) : capacity(other.capacity)
{
	if (capacity > 0)
	{
		data = static_cast< T* >(::operator new(capacity * sizeof(T)));
		distance_to_next_free = new size_t[capacity];
		std::copy(other.distance_to_next_free, other.distance_to_next_free + capacity, distance_to_next_free);
		for (size_t idx = 0; idx < capacity; ++idx)
		{
			if (distance_to_next_free[idx] != 0)
			{
				new (&data[idx]) T(other.data[idx]);
			}
		}
	}
	else
	{
		data = nullptr;
		distance_to_next_free = nullptr;
	}
}

template< typename T >
Block< T >::Block(Block&& other) noexcept : data(), distance_to_next_free(), capacity()
{
	swap(other);
}

template< typename T >
Block< T >& Block< T >::operator=(Block other)
{
	swap(other);
	return *this;
}

template< typename T >
void Block< T >::swap(Block& other) noexcept
{
	using std::swap;
	swap(data, other.data);
	swap(distance_to_next_free, other.distance_to_next_free);
	swap(capacity, other.capacity);
}

template< typename T >
void Block< T >::fix_distances() noexcept
{
	size_type prev = 0;
	for (size_t idx = capacity; idx-- > 0;)
	{
		if (distance_to_next_free[idx] == 0)
		{
			prev = 0;
		}
		else
		{
			distance_to_next_free[idx] = ++prev;
		}
	}
}

template< typename T >
bool Block< T >::is_full() const noexcept
{
	return capacity == 0 || distance_to_next_free[0] == capacity;
}

template< typename T >
bool Block< T >::is_empty() const noexcept
{
	for (size_t i = 0; i < capacity; ++i)
	{
		if (distance_to_next_free[i] != 0)
		{
			return false;
		}
	}
	return true;
}

template< typename T >
size_t Block< T >::insert(const T& value)
{
	if (distance_to_next_free[0] == capacity)
	{
		throw std::out_of_range("Block is full");
	}

	size_t idx = distance_to_next_free[0];
	new (&data[idx]) T(value);

	distance_to_next_free[idx] = 1;
	fix_distances();

	return idx;
}

template< typename T >
size_t Block< T >::insert(T&& value)
{
	if (distance_to_next_free[0] == capacity)
	{
		throw std::out_of_range("Block is full");
	}

	size_t idx = distance_to_next_free[0];
	new (&data[idx]) T(std::move(value));

	distance_to_next_free[idx] = 1;
	fix_distances();

	return idx;
}

template< typename T >
size_t Block< T >::erase(size_t idx)
{
	if (idx >= capacity || distance_to_next_free[idx] == 0)
	{
		throw std::out_of_range("Invalid index");
	}

	data[idx].~T();

	distance_to_next_free[idx] = 0;
	fix_distances();

	return idx + distance_to_next_free[idx];
}

template< typename T >
T& Block< T >::operator[](size_t idx)
{
	if (idx >= capacity || distance_to_next_free[idx] == 0)
	{
		throw std::out_of_range("Invalid index");
	}
	return data[idx];
}

template< typename T >
class BlockStorage;

// ITERATOR

template< typename T, typename ValType >
class Iterator
{
	void find_next();
	size_t block_capacity;

  public:
	using iterator_category = std::bidirectional_iterator_tag;
	using value_type = ValType;
	using difference_type = std::ptrdiff_t;
	using pointer = value_type*;
	using reference = value_type&;
	size_t getBlockIdx() const { return block_idx; }
	size_t getIdx() const { return idx; }

  private:
	friend class BlockStorage< T >;
	friend class Iterator< T, T >;
	friend class Iterator< T, const T >;
	vector< Block< T > >& blocks;
	bool is_end;
	size_t block_idx;
	size_t idx;

  public:
	Iterator(vector< Block< T > >& blocks, size_t block_capacity, size_t block_idx = 0, size_t idx = 0, bool is_end = false);
	Iterator(const Iterator& other);
	Iterator& operator=(const Iterator& other);

	Iterator& operator++();
	Iterator operator++(int);
	Iterator& operator--();
	Iterator operator--(int);

	value_type& operator*();
	value_type* operator->();

	template< typename U >
	bool operator==(const Iterator< T, U >& other) const noexcept;

	template< typename U >
	bool operator<(const Iterator< T, U >& other) const noexcept;

	template< typename U >
	bool operator<=(const Iterator< T, U >& other) const noexcept;

	template< typename U >
	bool operator!=(const Iterator< T, U >& other) const noexcept;

	template< typename U >
	bool operator>(const Iterator< T, U >& other) const noexcept;

	template< typename U >
	bool operator>=(const Iterator< T, U >& other) const noexcept;
};

template< typename T, typename value_type >
Iterator< T, value_type >::Iterator(vector< Block< T > >& blocks, size_t block_capacity, size_t block_idx, size_t idx, bool is_end) :
	blocks(blocks), block_idx(block_idx), idx(idx), block_capacity(block_capacity), is_end(is_end)
{
	if (!is_end && blocks[block_idx].distance_to_next_free[idx] == 0)
	{
		find_next();
	}
}

template< typename T, typename value_type >
Iterator< T, value_type >::Iterator(const Iterator< T, value_type >& other) :
	blocks(other.blocks), block_idx(other.block_idx), idx(other.idx), block_capacity(other.block_capacity), is_end(other.is_end)
{
}

template< typename T, typename value_type >
Iterator< T, value_type >& Iterator< T, value_type >::operator=(const Iterator< T, value_type >& other)
{
	if (this != &other)
	{
		blocks = other.blocks;
		block_idx = other.block_idx;
		idx = other.idx;
		block_capacity = other.block_capacity;
		is_end = other.is_end;
	}
	return *this;
}

template< typename T, typename value_type >
Iterator< T, value_type > Iterator< T, value_type >::operator++(int)
{
	Iterator< T, value_type > temp(*this);
	++(*this);
	return temp;
}

template< typename T, typename value_type >
Iterator< T, value_type > Iterator< T, value_type >::operator--(int)
{
	Iterator< T, value_type > temp(*this);
	--(*this);
	return temp;
}

template< typename T, typename value_type >
void Iterator< T, value_type >::find_next()
{
	while (block_idx < blocks.size())
	{
		while (++idx < block_capacity)
		{
			if (blocks[block_idx].distance_to_next_free[idx] != 0)
			{
				break;
			}
		}
		if (idx < block_capacity)
		{
			break;
		}
		idx = 0;
		++block_idx;
		if (block_idx < blocks.size() && blocks[block_idx].distance_to_next_free[idx] != 0)
		{
			break;
		}
	}

	if (block_idx >= blocks.size())
	{
		is_end = true;
	}
}

template< typename T, typename value_type >
Iterator< T, value_type >& Iterator< T, value_type >::operator++()
{
	if (is_end)
	{
		return *this;
	}

	if (block_capacity == 0)
	{
		throw std::out_of_range(
			"Can not increment this iterator (BucketStorage has capacity of 0 -- probably, bad "
			"initialization)");
	}

	find_next();

	return *this;
}

template< typename T, typename value_type >
Iterator< T, value_type >& Iterator< T, value_type >::operator--()
{
	if (is_end)
	{
		idx = block_capacity;
		block_idx = blocks.size() - 1;
		is_end = false;
	}

	if (idx == 0 && block_idx == 0)
	{
		throw std::out_of_range("Can not decrement");
	}

	if (block_capacity == 0)
	{
		throw std::out_of_range(
			"Can not decrement this iterator (BucketStorage has capacity of 0 -- probably, bad "
			"initialization)");
	}

	do
	{
		if (idx == 0)
		{
			--block_idx;
			idx = block_capacity - 1;
		}
		else
		{
			--idx;
		}
		if (block_idx == 0 && idx == 0)
		{
			break;
		}
	} while (blocks[block_idx].distance_to_next_free[idx] == 0);

	if (blocks[block_idx].distance_to_next_free[idx] != 0)
	{
		return *this;
	}

	throw std::out_of_range("Can not decrement");
}

template< typename T, typename value_type >
value_type& Iterator< T, value_type >::operator*()
{
	return blocks[block_idx][idx];
}

template< typename T, typename value_type >
value_type* Iterator< T, value_type >::operator->()
{
	return &blocks[block_idx][idx];
}

template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator==(const Iterator< T, U >& other) const noexcept
{
	return (this->is_end && other.is_end)
			 ? true
			 : (this->is_end == other.is_end && this->idx == other.idx && this->block_idx == other.block_idx);
}
template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator<(const Iterator< T, U >& other) const noexcept
{
	return !this->is_end &&
		   (other.is_end || this->block_idx < other.block_idx || this->block_idx == other.block_idx && this->idx < other.idx);
}
template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator<=(const Iterator< T, U >& other) const noexcept
{
	return !(other < *this);
}
template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator!=(const Iterator< T, U >& other) const noexcept
{
	return !((*this) == other);
}
template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator>(const Iterator< T, U >& other) const noexcept
{
	return !(*this > other);
}
template< typename T, typename value_type >
template< typename U >
bool Iterator< T, value_type >::operator>=(const Iterator< T, U >& other) const noexcept
{
	return !(*this < other);
}

template< typename T >
class BucketStorage
{
  public:
	using value_type = T;
	using reference = T&;
	using const_reference = const T&;
	using const_iterator = Iterator< T, const T >;
	using iterator = Iterator< T, T >;
	using size_type = size_t;
	using difference_type = ptrdiff_t;

  private:
	vector< Block< T > > blocks;
	size_t block_capacity;
	size_t last_active_idx;
	size_t number_of_elements;

	friend class Iterator< T, T >;
	friend class Iterator< T, const T >;

  public:
	BucketStorage(size_t block_capacity = 64);

	iterator insert(const BucketStorage< T >::value_type& value);

	iterator insert(BucketStorage< T >::value_type&& value);

	iterator erase(iterator it);
	iterator erase(const_iterator it);

	bool empty() const noexcept;
	size_t size() const noexcept;
	size_t capacity() const noexcept;

	void shrink_to_fit();

	void clear() noexcept;

	void swap(BucketStorage& other) noexcept;

	iterator begin() noexcept;
	const_iterator begin() const noexcept;
	const_iterator cbegin() noexcept;

	iterator end() noexcept;
	const_iterator end() const noexcept;
	const_iterator cend() noexcept;

	static iterator get_to_distance(iterator it, const ptrdiff_t distance);
};

template< typename T >
BucketStorage< T >::BucketStorage(size_t block_capacity) :
	block_capacity(block_capacity), last_active_idx(0), number_of_elements(0)
{
	blocks.push(Block< BucketStorage< T >::value_type >(block_capacity));
}

template< typename T >
void BucketStorage< T >::swap(BucketStorage& other) noexcept
{
	using std::swap;
	swap(this->blocks, other.blocks);
	swap(this->block_capacity, other.block_capacity);
	swap(this->last_active_idx, other.last_active_idx);
	swap(this->number_of_elements, other.number_of_elements);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::insert(const BucketStorage< T >::value_type& value)
{
	while (last_active_idx < blocks.size() && blocks[last_active_idx].is_full())
	{
		++last_active_idx;
	}
	if (last_active_idx >= blocks.size())
	{
		blocks.push(Block< BucketStorage< T >::value_type >(block_capacity));
	}
	size_t res = blocks[last_active_idx].insert(value);
	++number_of_elements;
	return BucketStorage< T >::iterator(this->blocks, block_capacity, last_active_idx, res, false);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::insert(BucketStorage< T >::value_type&& value)
{
	while (last_active_idx < blocks.size() && blocks[last_active_idx].is_full())
	{
		++last_active_idx;
	}

	if (last_active_idx >= blocks.size())
	{
		blocks.push(Block< BucketStorage< T >::value_type >(block_capacity));
	}
	size_t res = blocks[last_active_idx].insert(std::move(value));
	++number_of_elements;
	return BucketStorage< T >::iterator(this->blocks, block_capacity, last_active_idx, res, false);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::erase(BucketStorage< T >::const_iterator it)
{
	blocks[it.getBlockIdx()].erase(it.getIdx());
	--number_of_elements;
	return ++BucketStorage< T >::iterator(this->blocks, block_capacity, it.getBlockIdx(), it.getIdx(), false);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::erase(BucketStorage< T >::iterator it)
{
	blocks[it.getBlockIdx()].erase(it.getIdx());
	--number_of_elements;
	return ++BucketStorage< T >::iterator(this->blocks, block_capacity, it.getBlockIdx(), it.getIdx(), false);
}

template< typename T >
bool BucketStorage< T >::empty() const noexcept
{
	return number_of_elements == 0;
}

template< typename T >
size_t BucketStorage< T >::capacity() const noexcept
{
	size_t total_capacity = 0;
	for (size_t i = 0; i < blocks.size(); ++i)
	{
		if (!blocks[i].is_empty())
		{
			total_capacity += blocks[i].capacity;
		}
	}
	return total_capacity;
}

template< typename T >
size_t BucketStorage< T >::size() const noexcept
{
	return number_of_elements;
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::begin() noexcept
{
	return BucketStorage< T >::iterator(this->blocks, block_capacity, 0, 0, false);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::begin() const noexcept
{
	return BucketStorage< T >::const_iterator(this->blocks, block_capacity, 0, 0, false);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::cbegin() noexcept
{
	return BucketStorage< T >::const_iterator(this->blocks, block_capacity, 0, 0, false);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::end() noexcept
{
	return BucketStorage< T >::iterator(this->blocks, block_capacity, 0, 0, true);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::end() const noexcept
{
	return BucketStorage< T >::const_iterator(this->blocks, block_capacity, 0, 0, true);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::cend() noexcept
{
	return BucketStorage< T >::const_iterator(this->blocks, block_capacity, 0, 0, true);
}

template< typename T >
typename BucketStorage< T >::iterator
	BucketStorage< T >::get_to_distance(typename BucketStorage< T >::iterator it, const ptrdiff_t distance)
{
	ptrdiff_t temp = distance;
	while (temp > 0)
	{
		it++;
		temp--;
	}
	while (temp < 0)
	{
		it--;
		temp++;
	}
	return it;
}

template< typename T >
void BucketStorage< T >::shrink_to_fit()
{
	BucketStorage< T > temp(block_capacity);
	for (auto it = begin(); it != end(); ++it)
	{
		temp.insert(std::move(*it));
	}
	swap(temp);
}

template< typename T >
void BucketStorage< T >::clear() noexcept
{
	for (auto it = begin(); it != end(); ++it)
	{
		this->erase(it);
	}
}

#endif /* BUCKET_STORAGE_HPP */
