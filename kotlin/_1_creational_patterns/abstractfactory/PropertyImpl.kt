package _1_creational_patterns.abstractfactory

data class PropertyImpl(
    override val name: String,
    override val value: Any
) : Property

data class ServerConfigurationImpl(
    override val properties: List<Property>
) : ServerConfiguration

data class IntProperty(
    override val name: String,
    override val value: Int
) : Property

data class StringProperty(
    override val name: String,
    override val value: String
) : Property
