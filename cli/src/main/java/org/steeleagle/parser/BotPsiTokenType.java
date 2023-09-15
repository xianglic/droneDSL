package org.steeleagle.parser;

import com.intellij.psi.tree.IElementType;
import org.steeleagle.BotLanguage;

public class BotPsiTokenType extends IElementType {
  public BotPsiTokenType(String plus) {
    super(plus, BotLanguage.INSTANCE);
  }
}
