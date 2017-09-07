from django.shortcuts import render

from yourproject.models import Ingredient, Recept


def index(request):  
    name = request.get('naam')
    aantalCalorieen = request.get('calorieen')
    ingredienten = request.get('ingredienten')
    nodigeTijd = request.get('benodigdeTijd')

    recept = Recept(name = name, aantalCalorieen = aantalCalorieen, ingredienten = ingredienten, nodigeTijd = nodigeTijd)
    recept.save('toevoegen')