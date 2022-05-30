import {Component, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ActorFacade} from "../../modules/actor/actor.facade";
import {BibelleseFacade} from "../../modules/bibellese/bibellese.facade";
import {GetBibelleseRequest} from "../../modules/bibellese/getBibellese/get-bibellese-request";
import {AddBibelleseRequest} from "../../modules/bibellese/addBibellese/add-bibellese-request";
import {DeleteBibelleseRequest} from "../../modules/bibellese/deleteBibellese/delete-bibellese-request";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {FeatureManager} from "../../../assets/utils/feature.manager";

@Component({
  selector: 'app-bible',
  templateUrl: './bible.component.html',
  styleUrls: ['./bible.component.scss']
})

export class BibleComponent implements OnInit, OnDestroy {

  constructor(private bibelleseFacade: BibelleseFacade,
              private _snackBar: MatSnackBar,
              private actorFacade: ActorFacade,
              private featureManager: FeatureManager) {
  }

  displayedColumns: string[] = ['text', 'lieblingsvers', 'lieblingsversText', 'label', 'leser', 'kommentar', 'delete'];
  labelList: string[] = [];
  lieblingsverse: string[] = [];
  lieblingsversTexte: string[] = [];
  userAvailable: boolean = false
  onDestroy = new Subject()
  isExpanded = false;

  dataSource: any;

  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;

  ngOnInit() {
    this.getBibellese()
    this.actorFacade.stateActorIsValid$.pipe(takeUntil(this.onDestroy)).subscribe(r => {
      if (r) {
        this.userAvailable = true
      }
    })
  }

  ngOnDestroy() {
    this.onDestroy.next(null)
    this.onDestroy.complete()
  }

  getBibellese(): void {
    let request = new GetBibelleseRequest();
    this.bibelleseFacade.getBibellese(request)
    this.bibelleseFacade.stateGetBibelleseResponse$.pipe(takeUntil(this.onDestroy)).subscribe(result => {
      this.dataSource = new MatTableDataSource(result)
      this.dataSource.paginator = this.paginator;
    })
  }

  public form: FormGroup = new FormGroup({
    bibelabschnitt: new FormControl('', [Validators.required]),
    lieblingsvers: new FormControl('',),
    versText: new FormControl('',),
    labels: new FormControl(''),
    kommentar: new FormControl('', [Validators.required]),
    leser: new FormControl('', [Validators.required]),
  })

  prepareAddBibelleseRequest(request: AddBibelleseRequest) {
    request.bibelabschnitt = this.form.get("bibelabschnitt")?.value
    request.lieblingsverse = this.lieblingsverse
    request.versText = this.lieblingsversTexte
    request.labels = this.labelList
    request.kommentar = this.form.get("kommentar")?.value
    request.leser = this.form.get("leser")?.value
    return request;
  }

  addBibellese(): void {
    let request = new AddBibelleseRequest()
    this.prepareAddBibelleseRequest(request)
    this.bibelleseFacade.addBibellese(request);
    this.isExpanded = false;
  }

  deleteBibellese(element: any): void {
    let request = new DeleteBibelleseRequest()
    request.id = element.id
    this.bibelleseFacade.deleteBibellese(request)
  }

  addItemToList(itemToAdd: string, list: string[]) {
    if (!this.form.get(itemToAdd)?.value) {
      this.featureManager.openSnackbar("Value for " + itemToAdd + " must be filled", "failed")
      return;
    }
    list.push(this.form.get(itemToAdd)?.value);
    this.form.get(itemToAdd)?.reset();
  }

  deleteItemFromList(itemToDelete: string, list: string[]) {
    let count = 0;
    for (let item of list) {
      if (item === itemToDelete) {
        list.splice(count, 1)
        return
      }
      count++
    }
  }

  resetForm() {
    this.form.reset();
    this.labelList = [];
    this.lieblingsverse = [];
    this.lieblingsversTexte = [];
  }
}
