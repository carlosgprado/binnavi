/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.security.zynamics.reil.translators.arm;

import com.google.security.zynamics.reil.OperandSize;
import com.google.security.zynamics.reil.ReilHelpers;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.translators.ITranslationEnvironment;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.TranslationHelpers;
import com.google.security.zynamics.zylib.disassembly.IInstruction;
import com.google.security.zynamics.zylib.disassembly.IOperandTreeNode;

import java.util.List;


public class THUMBRev16Translator extends ARMBaseTranslator {
  @Override
  protected void translateCore(final ITranslationEnvironment environment,
      final IInstruction instruction, final List<ReilInstruction> instructions) {
    final IOperandTreeNode registerOperand1 =
        instruction.getOperands().get(0).getRootNode().getChildren().get(0);
    final IOperandTreeNode registerOperand2 =
        instruction.getOperands().get(1).getRootNode().getChildren().get(0);

    final String targetRegister = (registerOperand1.getValue());
    final String sourceRegister = (registerOperand2.getValue());

    final OperandSize dw = OperandSize.DWORD;

    long baseOffset = (instruction.getAddress().toLong() * 0x100) + instructions.size();

    final String tmpRm15to8 = environment.getNextVariableString();
    final String tmpRm23to16 = environment.getNextVariableString();
    final String tmpRm31to24 = environment.getNextVariableString();
    final String tmpRm7to0 = environment.getNextVariableString();
    final String tmpVar1 = environment.getNextVariableString();
    final String tmpVar2 = environment.getNextVariableString();
    final String tmpVar3 = environment.getNextVariableString();

    instructions.add(ReilHelpers.createAnd(baseOffset++, dw, sourceRegister, dw,
        String.valueOf(0x000000FFL), dw, tmpRm7to0));
    instructions.add(ReilHelpers.createBsh(baseOffset++, dw, tmpRm7to0, dw, String.valueOf(8), dw,
        targetRegister));

    instructions.add(ReilHelpers.createAnd(baseOffset++, dw, sourceRegister, dw,
        String.valueOf(0x0000FF00L), dw, tmpRm15to8));
    instructions.add(ReilHelpers.createBsh(baseOffset++, dw, tmpRm15to8, dw, String.valueOf(-8),
        dw, tmpVar1));
    instructions.add(ReilHelpers.createOr(baseOffset++, dw, targetRegister, dw, tmpVar1, dw,
        targetRegister));

    instructions.add(ReilHelpers.createAnd(baseOffset++, dw, sourceRegister, dw,
        String.valueOf(0x00FF0000L), dw, tmpRm23to16));
    instructions.add(ReilHelpers.createBsh(baseOffset++, dw, tmpRm23to16, dw, String.valueOf(8),
        dw, tmpVar2));
    instructions.add(ReilHelpers.createOr(baseOffset++, dw, targetRegister, dw, tmpVar2, dw,
        targetRegister));

    instructions.add(ReilHelpers.createAnd(baseOffset++, dw, sourceRegister, dw,
        String.valueOf(0xFF000000L), dw, tmpRm31to24));
    instructions.add(ReilHelpers.createBsh(baseOffset++, dw, tmpRm31to24, dw, String.valueOf(-8),
        dw, tmpVar3));
    instructions.add(ReilHelpers.createOr(baseOffset++, dw, targetRegister, dw, tmpVar3, dw,
        targetRegister));

  }

  @Override
  public void translate(final ITranslationEnvironment environment, final IInstruction instruction,
      final List<ReilInstruction> instructions) throws InternalTranslationException {
    TranslationHelpers.checkTranslationArguments(environment, instruction, instructions, "REV16");
    translateAll(environment, instruction, "REV16", instructions);

  }
}
