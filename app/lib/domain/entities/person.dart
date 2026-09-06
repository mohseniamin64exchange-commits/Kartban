enum PersonKind { male, female, company }

class Person {
  const Person({
    required this.id,
    required this.name,
    required this.kind,
    this.note,
    this.createdAt,
  });
  final int? id;
  final String name;
  final PersonKind kind;
  final String? note;
  final DateTime? createdAt;

  Person copyWith({int? id, String? name, PersonKind? kind, String? note}) =>
      Person(
        id: id ?? this.id,
        name: name ?? this.name,
        kind: kind ?? this.kind,
        note: note ?? this.note,
        createdAt: createdAt,
      );

  Map<String, Object?> toMap() => {
    'id': id,
    'name': name.trim(),
    'kind': kind.name,
    'note': note,
    'created_at': (createdAt ?? DateTime.now()).toIso8601String(),
  };

  factory Person.fromMap(Map<String, Object?> map) => Person(
    id: map['id'] as int?,
    name: map['name']! as String,
    kind: PersonKind.values.byName(map['kind']! as String),
    note: map['note'] as String?,
    createdAt: DateTime.tryParse(map['created_at'] as String? ?? ''),
  );
}
