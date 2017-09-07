from __future__ import unicode_literals

from django.db import models

# Create your models here.

from mongoengine import *

class Ingredient(EmbeddedDocument):
    name = StringField(max_length=200)

class Recept(Document):
    name = StringField(max_length=200)
    aantalCalorieen = IntField(default=0)
    Ingredienten = ListField(EmbeddedDocumentField(Ingredient))
    nodigeTijd = IntField(default=0)
