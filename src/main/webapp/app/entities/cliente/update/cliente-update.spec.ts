import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICliente } from '../cliente.model';
import { ClienteService } from '../service/cliente.service';

import { ClienteFormService } from './cliente-form.service';
import { ClienteUpdate } from './cliente-update';

describe('Cliente Management Update Component', () => {
  let comp: ClienteUpdate;
  let fixture: ComponentFixture<ClienteUpdate>;
  let activatedRoute: ActivatedRoute;
  let clienteFormService: ClienteFormService;
  let clienteService: ClienteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ClienteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clienteFormService = TestBed.inject(ClienteFormService);
    clienteService = TestBed.inject(ClienteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const cliente: ICliente = { id: 20795 };

      activatedRoute.data = of({ cliente });
      comp.ngOnInit();

      expect(comp.cliente).toEqual(cliente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICliente>();
      const cliente = { id: 13484 };
      vitest.spyOn(clienteFormService, 'getCliente').mockReturnValue(cliente);
      vitest.spyOn(clienteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cliente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cliente);
      saveSubject.complete();

      // THEN
      expect(clienteFormService.getCliente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clienteService.update).toHaveBeenCalledWith(expect.objectContaining(cliente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICliente>();
      const cliente = { id: 13484 };
      vitest.spyOn(clienteFormService, 'getCliente').mockReturnValue({ id: null });
      vitest.spyOn(clienteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cliente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cliente);
      saveSubject.complete();

      // THEN
      expect(clienteFormService.getCliente).toHaveBeenCalled();
      expect(clienteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICliente>();
      const cliente = { id: 13484 };
      vitest.spyOn(clienteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cliente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clienteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
