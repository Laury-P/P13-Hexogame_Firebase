# Projet 13 - Implémentez une base de données avec Firebase - Hexagonal Games

## 📌 Contexte
Projet réalisé dans le cadre de ma formation **OpenClassrooms – Développeur d’Application Android**.

L'enjeu principal de ce projet était l'externalisation de la logique de données. Initialement locale ou statique, l'application a été transformée pour devenir un produit connecté utilisant la puissance de la suite **Firebase**.

Le projet est **éducatif** et met l'accent sur la mise en place d'un backend scalable et sécurisé.

---

## 🎯 Objectifs pédagogiques
- **Implémenter l'authentification** : Gérer le cycle de vie utilisateur (inscription, connexion, déconnexion) via un fournisseur d'identité sécurisé.
- **Maîtriser Firestore** : Concevoir une base de données NoSQL distribuée, gérer les lectures/écritures et la synchronisation en temps réel.
- **Gérer le Cloud Storage** : Manipuler des fichiers médias (images) via un stockage distant.
- **Intégrer les Notifications Push** : Mettre en place Firebase Cloud Messaging pour la communication asynchrone vers l'utilisateur.

---

## ⚙️ Stack technique
- **Backend Service** : Firebase (Auth, Firestore, Storage, Cloud Messaging)
- **Langage** : Kotlin
- **Architecture** : Clean Architecture
- **Asynchronisme** : Coroutines & Flow
- **Injection de dépendances** : Dagger Hilt
- **Tests** : Tests unitaires (MockK & Turbine) validant l'intégrité des flux Firebase.

---

## 🧩 Modules Firebase Implémentés

### 🔐 Firebase Auth (via FirebaseUI)
- Flux d'authentification complet et sécurisé.
- Gestion des états de connexion via un `Flow<LocalAuthState>` pour une UI réactive.
- Fonctionnalité de suppression de compte avec gestion des exceptions (ré-authentification, erreurs réseau).

### 🗄️ Cloud Firestore (NoSQL)
- **Modélisation** : Structure en collections (`posts`) et sous-collections (`comments`).
- **Temps Réel** : Utilisation des `snapshots()` pour une mise à jour instantanée de l'interface.
- **Opérations Atomiques** : Utilisation des **Batched Writes** pour garantir la cohérence des données lors de suppressions en cascade (post et commentaires associés).

### 📁 Firebase Storage
- Upload sécurisé des images liées aux publications.
- Gestion des URLs de téléchargement pour l'affichage dynamique des médias.

### 🔔 Firebase Cloud Messaging (FCM)
- **Réception de notifications** : Configuration du service pour intercepter les messages Push en arrière-plan.
- **Gestion des Topics** : Implémentation d'un système d'abonnement/désabonnement dynamique au canal "all", permettant une diffusion de messages groupés.

---

## 🧠 Apprentissages et compétences développées
- **Architecture & Firebase** : Isolation des SDK Google derrière des interfaces de Repository pour respecter la Clean Architecture.
- **Programmation Réactive** : Transformation des callbacks de tâches Firebase en `Flow` Kotlin.
- **Gestion des flux Push** : Compréhension du fonctionnement des services (Services Android) pour la réception de messages via FCM.
- **Intégrité des données** : Nettoyage proactif du Cloud Storage et des sous-collections pour éviter les données orphelines.

---

## 🔍 Limites du projet
- **Messagerie Inter-utilisateurs** : Le système FCM actuel est configuré pour des notifications globales ; l'envoi de messages de pair à pair nécessiterait une logique serveur supplémentaire (Cloud Functions).
- **Règles de sécurité** : Les règles Firestore sont fonctionnelles pour le périmètre pédagogique mais mériteraient un durcissement pour une mise en production.

---

## 💡 Ouvertures et pistes d’amélioration
- **Cloud Functions** : Automatiser le nettoyage des données côté serveur plutôt que côté client.
- **Pagination** : Optimiser le chargement des listes de posts avec Firestore Cursors.
- **Deep Linking** : Utiliser les notifications FCM pour rediriger l'utilisateur vers un post spécifique de l'application.

---

## 📚 Ressources et références
- Cours OpenClassrooms : *Créez un backend scalable et performant sur Firebase*.
- Documentation officielle : [Firebase Cloud Messaging for Android](https://firebase.google.com/docs/cloud-messaging/android/client).

---

## 👤 Auteur
Projet réalisé individuellement par **Laury**, dans un cadre pédagogique.
