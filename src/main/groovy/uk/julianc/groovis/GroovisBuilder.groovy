package uk.julianc.groovis

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit

@Singleton
class GroovisBuilder {

    Map<String, List> composition = [:]
    boolean generated = false

    void add(SourceUnit sourceUnit) {
        sourceUnit.AST.classes.each { ClassNode classNode ->
            composition[classNode.name] = classNode.fields.collect { it.type.name }
        }
    }

    String generate() {
        generated = true
        def out = 'digraph {\n'
        out = composition.inject(out) { String acc, String className, List<String> dependsOn ->
            if (dependsOn) {
                dependsOn.inject(acc) { ds, d -> ds + "    $className -> $d;\n" }
            } else {
                acc + "    $className;\n"
            }
        }
        out + '}\n'
    }

    void clear() {
        composition = [:]
        generated = false
    }
}
