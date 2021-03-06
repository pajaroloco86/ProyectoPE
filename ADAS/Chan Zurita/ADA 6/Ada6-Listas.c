#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//ESTRUCTURA
typedef struct alarma{
    char hora[6];
    char medicina[15];
}alarma;

typedef struct nodo{
    alarma datos;
    int numNodo;
    struct nodo *sig;
} Nodo;

//PROTOTIPOS
Nodo *insertarAlarma(Nodo *lista);
Nodo *borrarAlarma(Nodo *lista, int numeroNodo);
void imprimirNodos(Nodo *lista);

//MAIN
int main(){
    Nodo *lista =  NULL;
    alarma datos;
    int numeNodo;
    int opcion = 1;

    while(opcion ==1){
        printf("Ingrese 1 para agregar alarma \n");
        printf("Ingrese 2 para imprimir todos las alarmas \n");
        printf("Ingrese 3 para eliminar una alarma \n");
        printf("Ingrese otro numero para terminar \n");
        scanf("%d", &opcion);
        switch (opcion){
            case 1: 
                lista = insertarAlarma(lista);
                break;
            case 2: 
                imprimirNodos(lista);
                opcion =1;
                break;
            case 3:
                printf("Ingrese el numero de alarma a borrar\n");
                scanf("%d", &numeNodo);
                printf("Nodos antes de borrar\n");
                imprimirNodos(lista);
                borrarAlarma(lista, numeNodo);
                printf("Nodos despues de borrar\n");
                imprimirNodos(lista);
                opcion =1;
                break;
            default:
                break;
        }
    }
    imprimirNodos(lista);

    printf("\nFin de operacion");

    return 0;
}

//FUNCIONES
Nodo *insertarAlarma(Nodo *lista){
    Nodo *nuevoNodo, *ultimoNodo;
    int numeroNodo = 1;
    char medicina[15], tiempo[6];
    nuevoNodo = (Nodo *) malloc(sizeof(Nodo));
    printf("Ingrese el nombre de la medicina\n");
    scanf("%s", &medicina);
    strcpy(nuevoNodo->datos.medicina, medicina);
    printf("Ingrese la hora de forma: 01:00\n");
    scanf("%s", &tiempo);
    strcpy(nuevoNodo ->datos.hora, tiempo);
    nuevoNodo->sig = NULL; 

    if(lista == NULL){
        lista = nuevoNodo;
        nuevoNodo->numNodo = numeroNodo;
    }else{
        ultimoNodo = lista; 
        while(ultimoNodo->sig != NULL){
            ultimoNodo = ultimoNodo ->sig;
            numeroNodo++; 
        }
        ultimoNodo->sig = nuevoNodo;
        nuevoNodo->numNodo = numeroNodo+1; 
    }

    return lista;
}

Nodo *borrarAlarma(Nodo *lista, int numeroNodo){
    Nodo *temporal;
    temporal = lista;

    if(temporal->numNodo == numeroNodo){
        lista = lista->sig;
        free(temporal); 
    }else{
        while((temporal ->sig)->numNodo != numeroNodo){
            temporal = temporal->sig;
        }
        Nodo *temporal2 = temporal->sig;
        temporal->sig = (temporal->sig)->sig;
        free(temporal2);
    }
    return lista;
}

void imprimirNodos(Nodo *lista){
    while (lista != NULL){
        printf("Nodo: %d  Medicina: %s Hora: %s \n", lista->numNodo, lista->datos.medicina,  lista->datos.hora);
        lista = lista->sig;
    }
    printf("\n\n\n");
}

/*
Mario Jesus Chan Zurita
*/


