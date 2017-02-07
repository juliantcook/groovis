package uk.julianc.groovis

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit

@Singleton
class GroovisBuilder {

    boolean generated = false
    Set<ClassNode> classes = []

    void add(SourceUnit sourceUnit) {
        classes.addAll(sourceUnit.AST.classes.findAll(this.&shouldAdd))
    }

    private boolean shouldAdd(ClassNode classNode) {
        isNotEnum(classNode)
    }

    private boolean isNotEnum(ClassNode classNode) {
        classNode.superClass.name != Enum.name
    }

    String generate() {
        generated = true
        def out = 'digraph {\n'
        out = compose().inject(out) { String acc, String className, List<String> dependsOn ->
            if (dependsOn) {
                dependsOn.inject(acc) { ds, d -> ds + "    $className -> $d;\n" }
            } else {
                acc + "    $className;\n"
            }
        }
        out + '}\n'
    }

    private Map<String, List> compose() {
        def allowedNames = classes*.name
        classes.collectEntries { ClassNode classNode ->
            [
                    (classNode.name): classNode.fields
                            .findAll { it.type.name in allowedNames }
                            .collect { it.type.name }
            ]
        }
    }

    void clear() {
        classes = []
        generated = false
    }
}
