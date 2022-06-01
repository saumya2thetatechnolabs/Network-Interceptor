package com.thetatechnolabs.foundation

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER
)
@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class Bindables
