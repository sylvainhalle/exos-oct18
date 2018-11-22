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

**A1.** En utilisant les processeurs `QueueSource` et `Add`, créez une chaîne de
processeurs qui retourne la somme de toutes les paires de nombres premiers
consécutifs. Ainsi, les cinq premiers événements produits devraient être 
5, 8, 12, 18, 24. Vous pouvez supposer que la liste nombres premiers est
connue. (Indice: essayez avec deux `QueueSource`.)

**A2.** En utilisant seulement les processeurs `Fork`, `Trim` et `ApplyFunction`,
écrivez un processeur qui calcule la somme de trois événements successifs.
(Indice: vous aurez besoin de deux instances de `Trim`.)

**A3.** Considérez un flux d'événements composé de nombres. On cherche à compter le
nombre de "sauts vers le bas" (en anglais *drops*) dans cette suite de
nombres. Pour les besoins de l'exercice, un saut vers le bas survient pour
l'événement e<sub>i</sub> si e<sub>i</sub> < e<sub>i-1</sub> et
e<sub>i-2</sub> < e<sub>i-1</sub>. Ainsi, pour la suite 1,2,3,2,1,4,2,6,7,...,
on devrait obtenir comme sortie: 0,0,0,1,1,1,2,2,2,...

**A4.** Écrivez une chaîne de processeurs qui calcule la variance coulissante
(*running variance*) d'un flux de valeurs numériques. La variance peut être
calculée par l'expression E[X²]-E[X]², où E[X] est la moyenne coulissante, et
E[X²] est la moyenne coulissante du carré de chaque valeur.

**A5.** Écrivez une chaîne de processeurs qui calcule le nombre de fois où la
valeur zéro apparaît dans une fenêtre coulissante de largeur *n* (pour un
entier *n* donné). Variante: modifiez la chaîne précédente pour traiter des
fenêtres disjointes: la première pour les événements 0 à *n*-1, l'autre pour
les événements *n* à 2*n*-1, et ainsi de suite.

Propriétés de sécurité
----------------------

Ce [dépôt Git](https://github.com/RacimBoussaha/TraceSamples) contient des
exemples d'enregistrements de traces d'exécution de programmes Java. Les
lignes de ces fichiers ressemblent à ceci:

```
1541009258085,668386784,call,NA,NA, ,SemaphoreTest.<clinit>,0,0,NA,NA
1541009258086,2065951873,call,NA,NA, ,java.util.concurrent.Semaphore.<init>,1,1,java.lang.Integer,4
1541009258086,2065951873,return,java.util.concurrent.Semaphore@3feba861[Permits = 4],0,NA,NA,NA,NA,NA,NA
1541009258087,1531448569,call,NA,NA, ,SemaphoreTest.semaphore,1,1,java.util.concurrent.Semaphore,java.util.concurrent.Semaphore@3feba861[Permits = 4]
1541009258087,1531448569,return,NA,0,NA,NA,NA,NA,NA,NA
1541009258085,668386784,return,NA,0,NA,NA,NA,NA,NA,NA
```

Chaque ligne commence par un horodatage (*timestamp*), suivi d'un identifiant
unique qui relie un appel au retour correspondant et par le mot-clé "call" ou
"return", identifiant le type de ligne. En cas de ligne d'appel, il listera
ensuite les informations suivantes, séparées par des virgules: la classe ou
l'interface et son package, un hashcode de l'objet appelant, le type de
retour, son nom, sa profondeur d'imbrication, le nombre de paramètres, une
liste de types de paramètres (séparés par `\`) et une liste de valeurs de
paramètres de même format. Une méthode de retour ne liste que le type et la
valeur de retour.

Cette information peut être utilisée pour vérifier des propriétés de sécurité
sur l'exécution d'un programme. Le [projet Naccio](http://naccio.cs.virginia.edu/code/policy-index.html)
liste près d'une centaines de telles propriétés. Ces propriétés peuvent être
vérifiées au moyen de chaînes de processeurs BeepBeep appropriées. Écrivez des
chaînes prenant en entrée des lignes de texte au format décrit ci-dessus,
produisant en sortie un flux de valeurs booléennes, et évaluant les
contraintes suivantes:

**B1.** À tout moment du programme, le nombre d'appels à la méthode *x* ne
dépasse jamais le nombre d'appels à la méthode *y* (pour *x* et *y* des noms
de méthodes paramétrables).

**B2.** La profondeur d'appel (i.e. le nombre d'appels imbriqués) ne dépasse
pas un certain seul *N*.

**B3.** Le nombre total d'octets écrits à travers des appels à la méthode
`write()` ne dépasse pas un certain seul *N*. (Indice: filtrez d'abord les
appels à la méthode `write`, et cumulez la taille de son argument.)

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

**C1.** Créez une chaîne de processeurs qui, à partir des lignes du log,
accumule les adresses IP trouvées dans un ensemble (vous aurez besoin de
[`SplitString`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/dictionary#strings)
et
[`PutInto`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#set-specific-objects)).

**C2.** Utilisez le processeur `TrendDistance` pour vérifier que, dans toute
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

**D1.** Écrivez une chaîne de processeurs qui calcule le nombre d'occurrences
de chaque premier chiffre non nul dans un flux de nombres en entrée, et en
tient le compte dans un tableau (`HashMap`). Ce tableau associe chaque chiffre
au nombre d'occurrences cumulatif. Vous aurez besoin des processeurs et
fonctions suivantes:

  - `FirstDigit` (fonction qui se trouve dans le dépôt de l'exercice)
  - `Counter` (processeur qui se trouve dans le dépôt de l'exercice)
  - [`Slice`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/core#slicing-a-stream)
  - [`PutInto`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#map-specific-objects)

**D2.** Utilisez le processeur `TrendDistance` pour vérifier si une source de
nombres générée aléatoirement respecte la loi de Benford. Les fréquences
d'occurrences attendues de chaque chiffre sont données
[ici](https://fr.wikipedia.org/wiki/Loi_de_Benford#Syst%C3%A8me_d%C3%A9cimal).

  - Dans cet exemple, quelle est la tendance de référence?
  - Quelle est la fonction de distance?
  - Quelle est la fonction de comparaison?
  - Quel serait un seuil (*threshold*) raisonnable?

**D3.** Écrivez un programme qui envoie des nombres aléatoires entre 1 et 99
au processeur instancié à l'étape précédente. Sur une fenêtre de largeur
**100**, la loi de Benford est-elle observée? L'est-elle davantage si on
augmente la longueur de la fenêtre? Expérimentez avec d'autres suites de
nombres, comme la suite des carrés (1, 2, 4, 9, ...) ou la [suite de
Fibonacci](https://fr.wikipedia.org/wiki/Suite_de_Fibonacci).

### Clustering

On considère une version "synthétique" d'un log tiré d'un pare-feu
[Cisco PIX](https://fr.wikipedia.org/wiki/Cisco_PIX). En particulier, on
s'intéresse aux connexions UDP, et plus précisément à leur
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

**E1.** Écrivez une chaîne de processeurs qui, à partir d'une séquence de
durées de sessions, accumule ces durées dans une liste (indice: utilisez
[`Pack`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/advanced#list-specific-objects)).
Ceci sera le processeur *beta* du workflow.

**E2.** Écrivez une chaîne de processeurs *alpha* qui, étant donné un ensemble
de séquences de durées, effectue le calcul suivant:

  - Fusionne tous les ensembles de durées extraits pour chaque séquence en une
    seule collection
  - Convertit les durées en instances de `DoublePoint` (indice: utilisez
    [`ApplyToAll`](https://liflab.gitbook.io/event-stream-processing-with-beepbeep-3/dictionary#applytoall))
  - Applique l'algorithme *k*-means, avec *k*=2, pour trouver les deux durées
    de sessions les plus "représentatives" de l'ensemble.

Modalités de remise
-------------------

D'ici le **21 décembre 2018**, envoyez par e-mail une archive respectant les
modalités suivantes:

- À la racine, un fichier texte appelé `Equipe.txt` contient les noms et
  matricules de chacun des membres de l'équipe
- Des dossiers nommés `A`, `B`, ... contiennent les bouts de code
  correspondant à chacune des questions précédentes.
- Autant que possible, chaque question devrait avoir une méthode `main()` qui
  illustre le fonctionnement de la chaîne de processeurs sur un exemple de
  flux d'entrée.

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