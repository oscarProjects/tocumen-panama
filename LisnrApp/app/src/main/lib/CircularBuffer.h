#ifndef RADIUSSAMPLE_CIRCULARBUFFER_H
#define RADIUSSAMPLE_CIRCULARBUFFER_H


#include <cstring>

namespace radiussample {

    template <class T>
    class CircularBuffer {
      public:
        explicit CircularBuffer(size_t capacity);
        CircularBuffer(CircularBuffer const &other) = delete;
        ~CircularBuffer();

        size_t size() const;
        size_t remaining() const;
        bool append(const T *buffer, size_t lenBuffer);
        bool remove(size_t count);
        bool read(size_t bufferOffset, T *dest, size_t count) const;

        T *m_buffer;
        size_t m_length;
        size_t m_rIndex;
        size_t m_wIndex;
    };


    template <class T> CircularBuffer<T>::CircularBuffer(size_t capacity) {
        ++ capacity;
        m_buffer = new T[capacity];
        m_length = capacity;
        m_rIndex = 0;
        m_wIndex = 0;
    }

    template <class T> CircularBuffer<T>::~CircularBuffer() {
        delete[] m_buffer;
    }

    template <class T>
    size_t CircularBuffer<T>::size() const {
        return (m_length + m_wIndex - m_rIndex) % m_length;
    }

    template <class T>
    size_t CircularBuffer<T>::remaining() const {
        return m_length - size() - 1;
    }

    template <class T>
    bool CircularBuffer<T>::append(const T *buffer, size_t lenBuffer) {
        // too large for buffer
        if (lenBuffer > remaining()) {
            return false;
        }

        if ((lenBuffer + m_wIndex) > m_length) {
            // we need to wrap around the end
            size_t tail = m_length - m_wIndex;

            // write tail
            memcpy(m_buffer + m_wIndex,
                   buffer,
                   sizeof(T) * tail);
            m_wIndex = 0;

            // write remainder
            memcpy(m_buffer + m_wIndex,
                   buffer + tail,
                   sizeof(T) * (lenBuffer - tail));

            // increment pointers
            m_wIndex += (lenBuffer - tail);
        } else {
            // write to buffer
            memcpy(m_buffer + m_wIndex,
                   buffer,
                   sizeof(T) * lenBuffer);

            // increment pointers
            m_wIndex = (m_wIndex + lenBuffer) % m_length;
        }

        return true;
    }

    template <class T>
    bool CircularBuffer<T>::remove(size_t count) {
        // reject removal request- invalid size
        if (count > size()) {
            return false;
        }

        m_rIndex = (m_rIndex + count) % m_length;
        return true;
    }

    template <class T>
    bool CircularBuffer<T>::read(size_t bufferOffset, T *dest, size_t count) const {
        if (bufferOffset + count > size()) {
            return false;
        }

        size_t startIndex = m_rIndex + bufferOffset;

        if (startIndex > m_length) {
            startIndex %= m_length;
            memcpy(dest, m_buffer + startIndex, sizeof(T) * count);
        } else {
            // check if overflow tail during read
            if (startIndex + count < m_length) {
                memcpy(dest, m_buffer + startIndex, sizeof(T) * count);
            } else {
                size_t tail = m_length - startIndex;
                memcpy(dest, m_buffer + startIndex, sizeof(T) * tail);
                memcpy(dest + tail, m_buffer, sizeof(T) * (count - tail));
            }
        }

        return true;
    }

}


#endif //RADIUSSAMPLE_CIRCULARBUFFER_H
