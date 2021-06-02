package com.razzzil.sphynx.commons.validation;

import java.util.Stack;

public interface Validated {
    Stack<ValidationResult> validate();
}
