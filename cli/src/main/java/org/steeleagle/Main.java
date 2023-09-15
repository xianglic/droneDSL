package org.steeleagle;

import kala.collection.immutable.ImmutableSeq;
import org.aya.intellij.GenericNode;
import org.steeleagle.parser.BotPsiElementTypes;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;

public class Main {
  public static int bro(GenericNode<? extends GenericNode<?>> node) {
    var seq = node.childrenOfType(BotPsiElementTypes.ADD_NUMBER).toImmutableSeq();
    var i = node.child(BotPsiElementTypes.NUMBER).tokenText().toInt();
    for (var child : seq) {
      i += child.child(BotPsiElementTypes.NUMBER).tokenText().toInt();
    }
    return i;
  }
  public static void main(String[] args) {
    var node = new DslParserImpl(new StreamReporter(System.out))
        .parseNode("123323 + 2 + 3 + 6");
    System.out.println(node.toDebugString());
    System.out.println(bro(node));
  }
}
