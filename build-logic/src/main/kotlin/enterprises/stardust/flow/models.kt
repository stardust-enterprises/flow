package enterprises.stardust.flow

interface Model<T> {
    fun consume0(target: T)
}

interface Activatable {
    var activated: Boolean
}