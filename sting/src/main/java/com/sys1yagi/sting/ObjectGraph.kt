package com.sys1yagi.sting

trait ObjectGraph {
    companion object {
        public fun create(vararg modules: Any): ObjectGraph {
            return Sting(modules)
        }
    }

    public fun <T> inject(target: T): Unit
    public fun <T> get(clazz: Class<T>): T
}
