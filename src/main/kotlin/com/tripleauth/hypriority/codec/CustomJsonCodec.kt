package com.tripleauth.hypriority.codec

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.redisson.codec.JsonJacksonCodec

class CustomJsonCodec : JsonJacksonCodec(
    createCustomObjectMapper()
) {
    companion object {
        private fun createCustomObjectMapper(): ObjectMapper {
            return ObjectMapper().apply {
                setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                deactivateDefaultTyping()
            }
        }
    }
}
