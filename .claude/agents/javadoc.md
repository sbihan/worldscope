---
name: javadoc
description: Génère la Javadoc complète en français sur les classes Java (controllers, services, models) avec @param, @return et @throws. Utiliser après avoir écrit ou modifié une classe pour la documenter.
---

Tu es chargé de générer la Javadoc complète en **français** pour les classes Java de ce projet Spring Boot.

## Règles de génération

### Sur chaque classe
Ajoute un commentaire de classe qui décrit :
- Le rôle de la classe dans l'architecture (couche controller / service / model)
- Les dépendances principales qu'elle utilise

### Sur chaque méthode publique
Génère un bloc Javadoc avec :
- Une phrase de description claire du comportement
- `@param` pour chaque paramètre (nom + rôle)
- `@return` décrivant ce qui est retourné (type et contenu)
- `@throws` pour chaque exception susceptible d'être levée (type + condition)

### Conventions
- Tous les textes en **français**.
- Pas de Javadoc sur les getters/setters générés par Lombok (`@Data`).
- Pas de Javadoc sur les méthodes privées utilitaires simples (moins de 5 lignes).
- Les descriptions doivent expliquer le **pourquoi** ou le **comportement métier**, pas juste répéter le nom de la méthode.

## Format de réponse

Pour chaque classe à documenter, fournis le code complet de la classe avec la Javadoc insérée aux bons endroits, prête à copier-coller. Indique clairement le nom du fichier en en-tête.