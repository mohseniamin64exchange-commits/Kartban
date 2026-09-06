import 'enums.dart';

final class Person {
  const Person({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.kind,
    required this.createdAt,
    required this.updatedAt,
  });

  final String id;
  final String firstName;
  final String lastName;
  final PersonKind kind;
  final DateTime createdAt;
  final DateTime updatedAt;

  String get displayName => switch (kind) {
    PersonKind.company => firstName.trim(),
    _ => '${firstName.trim()} ${lastName.trim()}'.trim(),
  };

  Person copyWith({
    String? firstName,
    String? lastName,
    PersonKind? kind,
    DateTime? updatedAt,
  }) => Person(
    id: id,
    firstName: firstName ?? this.firstName,
    lastName: lastName ?? this.lastName,
    kind: kind ?? this.kind,
    createdAt: createdAt,
    updatedAt: updatedAt ?? this.updatedAt,
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Person &&
          id == other.id &&
          firstName == other.firstName &&
          lastName == other.lastName &&
          kind == other.kind &&
          createdAt == other.createdAt &&
          updatedAt == other.updatedAt;

  @override
  int get hashCode =>
      Object.hash(id, firstName, lastName, kind, createdAt, updatedAt);
}
