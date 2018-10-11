Exercices 2018-10-12
====================

Installation
------------

- Télécharger la dernière version du noyau de BeepBeep (0.9):
  https://github.com/liflab/beepbeep-3/releases
- Télécharger la palette PatTheMiner:
  https://github.com/liflab/PatTheMiner/releases
- Télécharger la librairie Apache Commons Math 3:
  http://commons.apache.org/proper/commons-math
- (Optionnel) Télécharger l'archive des palettes:
  https://github.com/liflab/beepbeep-3-palettes/releases

Placer tous les fichiers JAR dans un répertoire et les inclure dans le
*classpath* de Java.

Traitement de flux d'événements
-------------------------------

Voici quelques exercices pour vous familiariser avec le principe du traitement
de flux d'événements et de la composition de processeurs dans BeepBeep.

1. En utilisant les processeurs `QueueSource` et `Add`, créez une chaîne de
processeurs qui retourne la somme de toutes les paires de nombres premiers
consécutifs. Ainsi, les cinq premiers événements produits devraient être 
5, 8, 12, 18, 24. Vous pouvez supposer que la liste nombres premiers est
connue. (Indice: essayez avec deux `QueueSource`.)

2. En utilisant seulement les processeurs `Fork`, `Trim` et `ApplyFunction`,
écrivez un processeur qui calcule la somme de trois événements successifs.
(Indice: vous aurez besoin de deux instances de `Trim`.)

3. Considérez un flux d'événements composé de nombres. On cherche à compter le
nombre de "sauts vers le bas" (en anglais *drops*) dans cette suite de
nombres. Pour les besoins de l'exercice, un saut vers le bas survient pour
l'événement e<sub>i</sub> si e<sub>i</sub> < e<sub>i-1</sub> et
e<sub>i-2</sub> < e<sub>i-1</sub>. Ainsi, pour la suite 1,2,3,2,1,4,2,6,7,...,
on devrait obtenir comme sortie: 0,0,0,1,1,1,2,2,2,...

4. Écrivez une chaîne de processeurs qui calcule la variance coulissante
(*running variance*) d'un flux de valeurs numériques. La variance peut être
calculée par l'expression E[X²]-E[X]², où E[X] est la moyenne coulissante, et
E[X²] est la moyenne coulissante du carré de chaque valeur.

5. Écrivez une chaîne de processeurs qui calcule le nombre de fois où la
valeur zéro apparaît dans une fenêtre coulissante de largeur *n* (pour un
entier *n* donné). Variante: modifiez la chaîne précédente pour traiter des
fenêtres disjointes: la première pour les événements 0 à *n*-1, l'autre pour
les événements *n* à 2*n*-1, et ainsi de suite.

Métriques et tendances
----------------------

Entrons maintenant dans le vif du sujet: le calcul de métriques et de
tendances diverses dans des flux d'événements.

### Nombres d'occurrences

On considère un log généré par l'exécution d'un serveur web Apache (le fichier
`access_log` qui se trouve dans
[cette archive](http://www.monitorware.com/en/logsamples/download/apache-samples.rar).
Une ligne de ce fichier ressemble à ceci:

    64.242.88.10 - - [07/Mar/2004:16:35:19 -0800] "GET /mailman/listinfo/business HTTP/1.1" 200 6379

La fonction `GetIp` (dans le dépôt des exercices) peut extraire l'adresse IP
(premier champ) de ces lignes de logs.

1. Créez une chaîne de processeurs qui, à partir des lignes du log, accumule
les adresses IP trouvées dans un ensemble (vous aurez besoin de
[`PutInto`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#set-specific-objects)).

2. Utilisez le processeur `TrendDistance` pour vérifier que, dans toute
fenêtre de 50 lignes consécutives, il y a au minimum 1 et au maximum 4
adresses distinctes.
  - Dans cet exemple, quelle est la tendance de référence?
  - Quelle est la fonction de distance?
  - Quelle est la fonction de comparaison?
  - Quelle est la largeur de la fenêtre?
  - Quel est le seuil (*threshold*)?

### Loi de Benford

En statistique, la [loi de Benford](https://fr.wikipedia.org/wiki/Loi_de_Benford)
stipule que, lorsqu'on étudie un ensemble de données, le premier chiffre
non-nul le plus fréquent est le 1; le second chiffre le plus fréquent est le
2, et ainsi de suite.

1. Écrivez une chaîne de processeurs qui calcule le nombre d'occurrences de
chaque premier chiffre non nul dans un flux de nombres en entrée, et en tient
le compte dans un tableau (`HashMap`). Ce tableau associe chaque chiffre au
nombre d'occurrences cumulatif. Vous aurez besoin des processeurs et fonctions
suivantes:
  - `FirstDigit` (fonction qui se trouve dans le dépôt de l'exercice)
  - `Counter` (processeur qui se trouve dans le dépôt de l'exercice)
  - [`Slice`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/core#slicing-a-stream)
  - [`PutInto`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#map-specific-objects)

2. Utilisez le processeur `TrendDistance` pour vérifier si une source de
nombres générée aléatoirement respecte la loi de Benford. Les fréquences
d'occurrences attendues de chaque chiffre sont données
[ici](https://fr.wikipedia.org/wiki/Loi_de_Benford#Syst%C3%A8me_d%C3%A9cimal).
  - Dans cet exemple, quelle est la tendance de référence?
  - Quelle est la fonction de distance?
  - Quelle est la fonction de comparaison?
  - Quel serait un seuil (*threshold*) raisonnable?

3. Écrivez un programme qui envoie des nombres aléatoires entre 1 et 99 au
processeur instancié à l'étape précédente. Sur une fenêtre de largeur **100**,
la loi de Benford est-elle observée? L'est-elle davantage si on augmente la
longueur de la fenêtre? Expérimentez avec d'autres suites de nombres, comme la
suite des carrés (1, 2, 4, 9, ...) ou la
[suite de Fibonacci](https://fr.wikipedia.org/wiki/Suite_de_Fibonacci).

### Clustering

On considère une version "synthétique" d'un log tiré d'un pare-feu
[Cisco PIX](https://fr.wikipedia.org/wiki/Cisco_PIX). En particulier, on
s'intéresse aux connexions UDP, et en particulier à leur
durée. Le début d'une connexion est représenté par une ligne comme celle-ci:

    Mar 29 2004 09:54:18: %PIX-6-302005: Built UDP connection for faddr 198.207.223.240/53337 gaddr 10.0.0.187/53 laddr 192.168.0.2/53

La fin d'une connection ressemble à ceci:

    Mar 29 2004 09:57:06: %PIX-6-302006: Teardown UDP connection for faddr 198.207.223.240/53337 gaddr 10.0.0.187/53 laddr 192.168.0.2/53

On peut déduire la durée d'une connexion en comparant les timestamps de ces
deux lignes pour une même adresse `faddr` (ici `198.207.223.240/53337`).
Supposons qu'un chaîne de processeurs BeepBeep fasse déjà ce pré-traitement,
et produise un flux de durées de sessions (en secondes).

On cherche à utiliser le clustering pour déterminer les durées de sessions
types dans un ensemble trace au moyen de l'objet [ProcessorMiningFunction]().

1. Écrivez une chaîne de processeurs qui, à partir d'une séquence de durées de
sessions, accumule ces durées dans une liste
(indice: utilisez [`Pack`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#list-specific-objects)).
Ceci sera le processeur *beta* du workflow.

2. Écrivez une chaîne de processeurs *alpha* qui fait ceci:



Références
----------

- [Event Stream Processing with BeepBeep 3](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3)
- [Real-Time Data Mining on Event Streams](https://www.researchgate.net/publication/328172038)
- [Diapositives de la présentation](https://www.slideshare.net/sylvainhalle/mining-event-streams-with-beepbeep-3)
- Javadoc de [BeepBeep](http://liflab.github.io/beepbeep-3/javadoc) et de
  [PatTheMiner](https://liflab.github.io/PatTheMiner) (cette dernière est en
  construction)
- [Exemples de code](https://liflab.github.io/beepbeep-3-examples)
- Site du [Laboratoire d'informatique formelle](http://liflab.ca) de l'UQAC

Contact
-------

[Sylvain Hallé](http://leduotang.ca/sylvain), Ph.D. Professeur titulaire,
[Université du Québec à Chicoutimi](http://www.uqac.ca). Titulaire de la
chaire de recherche du Canada en spécification, test et vérification de
systèmes informatiques. E-mail: `shalle@acm.org`.

<!-- :maxLineLen=78: -->