# Persistencia de récords y rankings

## Opción local (actual)

- Se usa localStorage para guardar puntajes y récords por usuario.

## Opción remota (recomendada)

- Usar Supabase o Firebase para rankings globales.
- Estructura sugerida:

```
record: {
  id: string,
  player: string,
  score: number,
  date: string
}
```

- Documentar endpoints y reglas de seguridad si se implementa backend.
