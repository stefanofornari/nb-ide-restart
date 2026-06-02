# Coding Standards

## Classes

## Methods & Variables
- Avoid static methods whever possible
- Use final variables and define variables final unless their value shall change

## Tests
- Use JUnit 4 or 5 depending on what the project already uses
- Use AssertJ as assertions framework
- Use BDDAssertions' then() as opposed to assertThat()
- When writing tests, use snake_case for the name of the test methods (i.e. use this_is_a_test as opposed to thisIsATest).
- When writing tests, do not prefix the test method with any text (i.e. use this_is_a_test as opposed to test_this is_a_test).
- When writing tests, use affirmative names for test methods instead of using 'should'. For example, use 'addPropertyChangeListener_does_not_accept_null' instead of 'addPropertyChangeListener_should_not_add_null_listener'.

## Common Frameworkds
- Use apache commons library if helpful
