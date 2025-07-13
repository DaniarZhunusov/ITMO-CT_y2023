#include <cmath>

template< typename T >
struct matrix_t
{
	T data[16];
};

template< typename T >
struct vector3_t
{
	T x, y, z;
};

template< typename T >
class Quat
{
  public:
	Quat() : m_value{ 0 } {}

	Quat(T a, T b, T c, T d) : m_value{ b, c, d, a } {}

	Quat(T angle, bool is_rad, vector3_t< T > coords)
	{
		angle = is_rad ? (angle * 0.5) : (angle * M_PI / 360);

		vector3_t< T > unit_vector = normalize_vector(coords);

		this->m_value[3] = std::cos(angle);
		this->m_value[0] = unit_vector.x * std::sin(angle);
		this->m_value[1] = unit_vector.y * std::sin(angle);
		this->m_value[2] = unit_vector.z * std::sin(angle);
	}

	const T *data() const { return m_value; }

	Quat operator*(const Quat &other) const noexcept
	{
		return Quat{
			this->m_value[3] * other.m_value[3] - this->m_value[0] * other.m_value[0] -
				this->m_value[1] * other.m_value[1] - this->m_value[2] * other.m_value[2],
			this->m_value[3] * other.m_value[0] + this->m_value[0] * other.m_value[3] +
				this->m_value[1] * other.m_value[2] - this->m_value[2] * other.m_value[1],
			this->m_value[3] * other.m_value[1] - this->m_value[0] * other.m_value[2] +
				this->m_value[1] * other.m_value[3] + this->m_value[2] * other.m_value[0],
			this->m_value[3] * other.m_value[2] + this->m_value[0] * other.m_value[1] -
				this->m_value[1] * other.m_value[0] + this->m_value[2] * other.m_value[3]
		};
	}

	Quat operator*(T scalar) const noexcept
	{
		return Quat{
			this->m_value[3] * scalar,
			this->m_value[0] * scalar,
			this->m_value[1] * scalar,
			this->m_value[2] * scalar,
		};
	}

	Quat operator*(vector3_t< T > vec) const noexcept { return this->operator*(Quat{ 0, vec.x, vec.y, vec.z }); }

	Quat operator+(const Quat &other) const noexcept
	{
		return Quat{
			this->m_value[3] + other.m_value[3],
			this->m_value[0] + other.m_value[0],
			this->m_value[1] + other.m_value[1],
			this->m_value[2] + other.m_value[2],
		};
	}

	Quat operator-(const Quat &other) const noexcept
	{
		return Quat{
			this->m_value[3] - other.m_value[3],
			this->m_value[0] - other.m_value[0],
			this->m_value[1] - other.m_value[1],
			this->m_value[2] - other.m_value[2],
		};
	}

	Quat &operator+=(const Quat &other) noexcept
	{
		for (int i = 0; i < 4; ++i)
		{
			this->m_value[i] += other.m_value[i];
		}
		return *this;
	}

	Quat &operator-=(const Quat &other) noexcept
	{
		for (int i = 0; i < 4; ++i)
		{
			this->m_value[i] -= other.m_value[i];
		}
		return *this;
	}

	Quat operator~() const { return Quat(this->m_value[3], -this->m_value[0], -this->m_value[1], -this->m_value[2]); }

	explicit operator T() const
	{
		T res = 0;

		for (int i = 0; i < 4; ++i)
		{
			res += this->m_value[i] * this->m_value[i];
		}

		return std::sqrt(res);
	}

	matrix_t< T > rotation_matrix() const noexcept
	{
		const Quat< T > normalized = this->normalized_quaternion();

		return matrix_t< T >{
			1 - 2 * normalized.m_value[1] * normalized.m_value[1] - 2 * normalized.m_value[2] * normalized.m_value[2],

			2 * normalized.m_value[0] * normalized.m_value[1] + 2 * normalized.m_value[3] * normalized.m_value[2],

			2 * normalized.m_value[0] * normalized.m_value[2] - 2 * normalized.m_value[3] * normalized.m_value[1],

			0,

			2 * normalized.m_value[0] * normalized.m_value[1] - 2 * normalized.m_value[3] * normalized.m_value[2],

			1 - 2 * normalized.m_value[0] * normalized.m_value[0] - 2 * normalized.m_value[2] * normalized.m_value[2],

			2 * normalized.m_value[1] * normalized.m_value[2] + 2 * normalized.m_value[3] * normalized.m_value[0],

			0,

			2 * normalized.m_value[0] * normalized.m_value[2] + 2 * normalized.m_value[3] * normalized.m_value[1],

			2 * normalized.m_value[1] * normalized.m_value[2] - 2 * normalized.m_value[3] * normalized.m_value[0],

			1 - 2 * normalized.m_value[0] * normalized.m_value[0] - 2 * normalized.m_value[1] * normalized.m_value[1],
			0,
			0,
			0,
			0,
			1
		};
	}

	matrix_t< T > matrix() const noexcept
	{
		return matrix_t< T >{
			this->m_value[3], -this->m_value[0], -this->m_value[1], -this->m_value[2],
			this->m_value[0], this->m_value[3],	 -this->m_value[2], this->m_value[1],
			this->m_value[1], this->m_value[2],	 this->m_value[3],	-this->m_value[0],
			this->m_value[2], -this->m_value[1], this->m_value[0],	this->m_value[3]
		};
	}

	T angle(bool is_rad = true) const
	{
		const T length = static_cast< T >(*this);
		if (length == 0)
		{
			return 0;
		}

		T angle = std::acos(2 * this->m_value[3] / length) * 2;
		if (!is_rad)
		{
			angle = angle / 360 * M_PI;
		}

		return angle;
	}

	vector3_t< T > apply(vector3_t< T > vec) const noexcept
	{
		const matrix_t< T > rot = this->rotation_matrix();
		vector3_t< T > res;
		T vec_data[3] = { vec.x, vec.y, vec.z };
		T res_data[3] = { 0 };

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 3; ++j)
			{
				res_data[i] += rot.data[j * 4 + i] * vec_data[j];
			}
		}

		return vector3_t< T >{ res_data[0], res_data[1], res_data[2] };
	}

	bool operator==(const Quat &other) const noexcept
	{
		for (int i = 0; i < 4; ++i)
		{
			if (!equals(this->m_value[i], other.m_value[i]))
			{
				return false;
			}
		}
		return true;
	}

	bool operator!=(const Quat &other) const noexcept { return !this->operator==(other); }

  private:
	T m_value[4];

	static constexpr T EPS = 1e-9;

	static bool abs(T a, T b) noexcept { return a > b ? a - b : b - a; }

	static bool equals(T a, T b) noexcept { return abs(a, b) < EPS; }

	static vector3_t< T > normalize_vector(const vector3_t< T > &vec) noexcept
	{
		const T length = std::sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);

		if (length == 0)
		{
			return vec;
		}

		return vector3_t< T >{ vec.x / length, vec.y / length, vec.z / length };
	}

	Quat normalized_quaternion() const noexcept
	{
		const T length = static_cast< T >(*this);
		if (length == 0)
		{
			return Quat();
		}

		return Quat(this->m_value[3] / length, this->m_value[0] / length, this->m_value[1] / length, this->m_value[2] / length);
	}
};
